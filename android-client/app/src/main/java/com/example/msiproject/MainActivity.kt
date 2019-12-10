package com.example.msiproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.msiproject.local.ChangeQuantityLocallyTask
import com.example.msiproject.local.LoadItemsFromDBTask
import com.example.msiproject.local.Local
import com.example.msiproject.local.SaveItemsLocallyTask
import com.example.msiproject.utils.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity(), IStockItemAction, Request.IRequestResult {

    override fun publishProblem(e: Exception?) {
        runOnUiThread { Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show() }
    }

    var isOffline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshBtn.setOnClickListener{refresh(it)}

        isOffline = intent.getBooleanExtra("offline", false)
        offlineRetry.setOnClickListener {
            offlineRetry.isEnabled = false
            synchronize()
        }

        setOfflineMode()
        synchronize()

        userStatus.text = Tokens.getToken(this).substring(0,8) + " - level " + getUserLevel()

    }

    private fun setOfflineMode() {
        if(isOffline) {
            offlineMode.visibility = View.VISIBLE
            offlineRetry.isEnabled = true
        } else {
            offlineMode.visibility = View.GONE
        }
    }

    private fun synchronize() {
        object: AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                val actions = Local.getActions(this@MainActivity)
                if(!actions.isEmpty()) {
                    val om = ObjectMapper()
                    val str = om.writeValueAsString(actions)
                    val result = Request(
                        Constants.SERVER_DEST(null) + "/sync",
                        "POST",
                        str,
                        mapOf(Constants.TOKEN_HEADER to Tokens.getToken(this@MainActivity)),
                        this@MainActivity,
                        Request.Signal.Synchronize
                    ).doRequestSync()
                    this@MainActivity.publishResult(result, Request.Signal.Synchronize)
                }
                return null
            }

            override fun onPostExecute(result: Void?) {

            }
        }.execute()
    }

    fun populateList(data: Array<ItemModel>) {
        val itemsAdapter = ItemsAdapter(this, R.layout.stock_item, data, this)
        list.adapter = itemsAdapter
    }

    fun refresh(v: View) {
        if(isOffline) {
            LoadItemsFromDBTask(this, this).execute()
            return
        }
        v.isEnabled = false
        Request(Constants.SERVER_DEST(null)+"/", "GET", "{}", mapOf(Constants.TOKEN_HEADER to Constants.TEST_TOKEN), this, Request.Signal.Fetch).execute()
    }

    override fun publishResult(data: RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Fetch -> {
                runOnUiThread{refreshBtn.isEnabled = true}
                if(data != null && data.resultCode < 300 && data.resultCode >= 200) {
                    val objectMapper = ObjectMapper()
                    val items: List<ItemModel> = objectMapper.readValue(data.data)
                    SaveItemsLocallyTask(items, this).execute()
                    runOnUiThread { populateList(items.toTypedArray()) }
                }
            }

            Request.Signal.Synchronize, Request.Signal.Quantity, Request.Signal.Delete, Request.Signal.Add, Request.Signal.Edit -> {
                if(data != null && data.resultCode < 300 && data.resultCode >= 200) {
                    runOnUiThread { if(refreshBtn.isEnabled) refresh(refreshBtn) }
                }

                if(sig == Request.Signal.Synchronize)
                    runOnUiThread {
                        isOffline = false
                        setOfflineMode()
                    }
            }

        }
    }

    override fun deleteItem(id: Int) {
        Request(
            Constants.SERVER_DEST(null)+"/"+id,
            "DELETE",
            "",
            mapOf(Constants.TOKEN_HEADER to Tokens.getToken(this)),
            this,
            Request.Signal.Delete
        ).execute()
    }

    override fun editItem(id: Int, model: ItemModel?) {
        val intent = Intent(this, AddEditActivity::class.java)
        if(model != null) {
            intent.putExtras(model.asBundle())
        }
        startActivityForResult(intent, Constants.ACTIVITY_REQUEST_ADDEDIT)
    }

    override fun quantity(id: Int, delta: Int) {
        if(isOffline) {
            ChangeQuantityLocallyTask(this, this, id, delta).execute()
            return
        }
        Request(
            Constants.SERVER_DEST(null)+"/"+id,
            "PUT",
            "{\"item\":"+id+",\"change\":"+delta+"}",
            mapOf(Constants.TOKEN_HEADER to Tokens.getToken(this)),
            this,
            Request.Signal.Quantity
        ).execute()
    }

    override fun canDelete(): Boolean = getUserLevel() >= 4
    override fun canEdit(): Boolean = getUserLevel() >= 2
    override fun canQuantity(): Boolean = getUserLevel() >=1
    fun getUserLevel(): Int = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE).getInt(Constants.USER_LEVEL, 0)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Constants.ACTIVITY_REQUEST_ADDEDIT) {
            if(resultCode == Constants.ACTIVITY_RESULT_OK)
                runOnUiThread { if(refreshBtn.isEnabled) refresh(refreshBtn) }
            else if(resultCode == Constants.ACTIVITY_RESULT_FAIL)
                publishProblem(Exception("Add/Edit resulted in failure"))
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}
