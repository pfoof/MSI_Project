package com.example.msiprojectv2

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.msiprojectv2.local.*
import com.example.msiprojectv2.utils.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity(), IStockItemAction, Request.IRequestResult {

    override fun publishProblem(e: Exception?) {
        runOnUiThread { Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show() }
    }

    var isOffline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshBtn.setOnClickListener{ refresh(it) }

        if(canEdit())
            addBtn.show()
        else
            addBtn.hide()

        isOffline = intent.getBooleanExtra("offline", false)
        offlineRetry.setOnClickListener {
            offlineRetry.isEnabled = false
            synchronize()
        }

        setOfflineMode()
        if(!isOffline)
            synchronize()

        addBtn.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            if(isOffline)
                intent.putExtra("offline", isOffline)
            startActivityForResult(intent, Constants.ACTIVITY_REQUEST_ADDEDIT)
        }

        userStatus.text = Tokens.getToken(this).substring(0,8) + " - level " + getUserLevel()

    }

    private fun setOfflineMode() {
        if(isOffline) {
            offlineMode.visibility = View.VISIBLE
            offlineRetry.isEnabled = true
        } else {
            offlineMode.visibility = View.GONE
            offlineRetry.isEnabled = false
        }
    }

    private fun synchronize() {
        object: AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                val prefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE)
                val uuid = if(prefs.contains(Constants.SYNC_UUID)) prefs.getString(Constants.SYNC_UUID, UUID.randomUUID().toString()) else UUID.randomUUID().toString()
                prefs.edit().putString(Constants.SYNC_UUID, uuid).commit()
                val actions = Local.getActions(this@MainActivity)
                if(!actions.isEmpty()) {
                    val str = SyncPack(uuid!!, actions).toString()
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
            object: AsyncTask<Void?, Void?, Void?>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    val actions = Local.getActions(this@MainActivity)
                    runOnUiThread {
                        val adapter = ActionsAdapter(this@MainActivity, R.layout.action_item, actions)
                        actionsList.adapter = adapter
                    }
                    return null
                }
            }.execute()
            return
        }
        v.isEnabled = false
        Request(Constants.SERVER_DEST(null)+"/", "GET", "{}", mapOf(Constants.TOKEN_HEADER to Constants.TEST_TOKEN), this, Request.Signal.Fetch).execute()
    }

    override fun publishResult(data: RequestResult?, sig: Request.Signal?) {

        //For connection failure -> go offline
        if(data != null && data.resultCode < -100) {
            isOffline = true
            runOnUiThread { setOfflineMode() }
            return
        }

        when(sig) {

            Request.Signal.Authorize -> {
                if(data != null && data.resultCode >= 200 && data.resultCode < 300) {
                    if(isOffline) {
                        isOffline = false
                        setOfflineMode()
                        synchronize()
                    }
                } else {
                    setResult(Constants.ACTIVITY_RESULT_FAIL)
                    finish()
                }
                return
            }

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

                if(sig == Request.Signal.Synchronize && data != null && data.resultCode >= 200 && data.resultCode < 300) {
                    Local.deleteAllActions(this)
                    getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE).edit().remove(Constants.SYNC_UUID).commit()
                    runOnUiThread {
                        isOffline = false
                        setOfflineMode()
                        Toast.makeText(this, "Synchronized!!!", Toast.LENGTH_SHORT).show()
                        Log.d("Synchronize", "Done with synchronization")
                    }
                }
            }

        }
    }

    override fun deleteItem(id: Int) {

        if(isOffline) {
            DeleteItemLocallyTask(this, this, id).execute()
            return
        }

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
            if(isOffline)
                intent.putExtra("offline", isOffline)
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

    override fun onBackPressed() {
        setResult(Constants.ACTIVITY_RESULT_OK)
        super.onBackPressed()
    }
}
