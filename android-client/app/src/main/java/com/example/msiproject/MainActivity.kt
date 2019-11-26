package com.example.msiproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.msiproject.utils.*
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity(), IStockItemAction, Request.IRequestResult {

    override fun publishProblem(e: Exception?) {
        runOnUiThread { Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshBtn.setOnClickListener{refresh(it)}
    }

    fun populateList(data: Array<ItemModel>) {
        val itemsAdapter = ItemsAdapter(this, R.layout.stock_item, data, this)
        list.adapter = itemsAdapter
    }

    fun refresh(v: View) {
        v.isEnabled = false
        Request(Constants.SERVER_DEST+"/", "GET", "{}", mapOf(Constants.TOKEN_HEADER to Constants.TEST_TOKEN), this, Request.Signal.Fetch).execute()
    }

    override fun publishResult(data: Request.RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Fetch -> {
                runOnUiThread{refreshBtn.isEnabled = true}
                if(data != null && data.resultCode < 300 && data.resultCode >= 200) {
                    val objectMapper = ObjectMapper()
                    val items: List<ItemModel> = objectMapper.readValue(data.data)
                    runOnUiThread { populateList(items.toTypedArray()) }
                }
            }

            Request.Signal.Quantity, Request.Signal.Delete, Request.Signal.Add, Request.Signal.Edit -> {
                if(data != null && data.resultCode < 300 && data.resultCode >= 200) {
                    runOnUiThread { if(refreshBtn.isEnabled) refresh(refreshBtn) }
                }
            }


        }
    }

    override fun deleteItem(id: Int) {
        Request(
            Constants.SERVER_DEST+"/"+id,
            "DELETE",
            "",
            mapOf(Constants.TOKEN_HEADER to Constants.TEST_TOKEN),
            this,
            Request.Signal.Delete
        ).execute()
    }

    override fun editItem(id: Int, model: ItemModel?) {
        val intent = Intent(this, AddEditActivity::class.java)
        if(model != null) {
            intent.putExtras(model.asBundle())
        }
        startActivity(intent)
    }

    override fun quantity(id: Int, delta: Int) {
        Request(
            Constants.SERVER_DEST+"/"+id,
            "PUT",
            "{\"delta\":\""+delta+"\"}",
            mapOf(Constants.TOKEN_HEADER to Constants.TEST_TOKEN),
            this,
            Request.Signal.Quantity
        ).execute()
    }

    override fun canDelete(): Boolean {
        return false
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canEdit(): Boolean {
        return false
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canQuantity(): Boolean {
        return true
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
