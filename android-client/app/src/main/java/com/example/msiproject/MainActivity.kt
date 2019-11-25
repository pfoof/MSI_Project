package com.example.msiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.example.msiproject.utils.*
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IStockItemAction, Request.IRequestResult {

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
        Request(Constants.SERVER_DEST+"/", "GET", "", null, this, Request.Signal.Fetch).execute()
    }

    override fun publishResult(data: Request.RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Fetch -> {
                runOnUiThread{refreshBtn.isEnabled = true}
                if(data != null && data.resultCode < 300 && data.resultCode >= 200) {
                    val objectMapper = ObjectMapper()
                    val items: List<ItemModel> = objectMapper.readValue(data.data)
                    populateList(items.toTypedArray())
                }
            }
        }
    }

    override fun deleteItem(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun editItem(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun quantity(id: Int, delta: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canDelete(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canEdit(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canQuantity(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
