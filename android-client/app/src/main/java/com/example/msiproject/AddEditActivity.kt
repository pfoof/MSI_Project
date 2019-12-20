package com.example.msiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.msiproject.local.EditItemLocallyTask
import com.example.msiproject.utils.*
import com.fasterxml.jackson.core.JsonProcessingException
import kotlinx.android.synthetic.main.activity_add_edit.*
import java.lang.Exception

class AddEditActivity : AppCompatActivity(), Request.IRequestResult {

    var id: Int = -1
    var isOffline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        val bun = intent.extras ?: Bundle()
        id = bun.getInt("item", -1)
        isOffline = bun.getBoolean("offline", false)

        inputQuantity.isEnabled = !bun.containsKey("item")
        inputQuantity.setText( ""+bun.getInt("quantity", 1) )
        inputName.setText( bun.getString("name") ?: "polaris" )
        inputProd.setText( bun.getString("prod") ?: "htc")
        inputPrice.setText( ""+bun.getFloat("price", 19.99f) )
        itemID.setText( "ID: " + ( if(id>0) id else "<new item>") )
        unshowBusy()

        cancel.setOnClickListener{ setResult(Constants.ACTIVITY_RESULT_CANCEL); this.finish() }
        save.setOnClickListener{
            showBusy()
            if(isOffline)
                sendToLocal()
            else
                sendToServer()
        }
    }

    override fun publishResult(data: RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Edit, Request.Signal.Add -> {
                runOnUiThread { unshowBusy() }
                if(data != null) {
                    if(data.resultCode >= 200 && data.resultCode < 300)
                        runOnUiThread {
                            setResult(Constants.ACTIVITY_RESULT_OK)
                            finish()
                        }
                    else {
                        runOnUiThread {
                            setResult(Constants.ACTIVITY_RESULT_FAIL)
                            Toast.makeText(this, ""+data.resultCode+"/"+data.data, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

    override fun publishProblem(e: Exception?) {
        runOnUiThread{
            Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show()
            unshowBusy()
        }
    }

    private fun showBusy() {
        cancel.isEnabled = false
        save.isEnabled = false
        inputName.isEnabled = false
        inputQuantity.isEnabled = false
        inputProd.isEnabled = false
        inputPrice.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

    private fun unshowBusy() {
        inputQuantity.isEnabled = id <= 0
        cancel.isEnabled = true
        save.isEnabled = true
        inputName.isEnabled = true
        inputProd.isEnabled = true
        inputPrice.isEnabled = true
        progressBar.visibility = View.GONE
    }

    private fun sendToLocal() {
        val im = ItemModel()
        im.name = inputName.text.toString()
        im.prod = inputProd.text.toString()
        im.price = inputPrice.text.toString().toFloat()
        if(id <= 0) {
            im.quantity = inputQuantity.text.toString().toInt()
        } else {
            im.id = id;
        }

        EditItemLocallyTask(this, this, id, im).execute()
    }

    private fun sendToServer() {
        val im = ItemModel()
        var method = "POST"
        var signal = Request.Signal.Add
        im.name = inputName.text.toString()
        im.prod = inputProd.text.toString()
        im.price = inputPrice.text.toString().toFloat()
        if(id <= 0) {
            im.quantity = inputQuantity.text.toString().toInt()
            method = "POST"
            signal = Request.Signal.Add
        } else {
            im.id = id.toString().toInt()
            method = "PUT"
            signal = Request.Signal.Edit
        }

        try {
            val asjson = im.toJson()
            Log.d("AddEdit", asjson)
            Request(Constants.SERVER_DEST(null)+"/", method, asjson, mapOf("Content-Type" to "application/json", Constants.TOKEN_HEADER to Tokens.getToken(this)), this, signal).execute()
        } catch (e: JsonProcessingException) {
            publishProblem(e)
        }
    }
}
