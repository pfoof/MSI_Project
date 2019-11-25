package com.example.msiproject

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
        Request(Constants.SERVER_DEST+"/", "GET", "{}", mapOf(Constants.TOKEN_HEADER to "D2096F0CAAF5E7C425CBCBF967DDB2619A29C0530662801D95B063B3B6EE2759EA3A46D4F3274BEFAB36B5AD417A129C1E0C3DD7FCBF56702EB3B39EA5102FE8"), this, Request.Signal.Fetch).execute()
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
