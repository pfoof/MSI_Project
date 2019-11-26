package com.example.msiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.msiproject.utils.Constants
import com.example.msiproject.utils.Request
import kotlinx.android.synthetic.main.activity_add_edit.*
import java.lang.Exception

class AddEditActivity : AppCompatActivity(), Request.IRequestResult {

    var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        val bun = intent.extras ?: Bundle()
        inputQuantity.isEnabled = !bun.containsKey("quantity")
        inputQuantity.setText( bun.getString("quantity") ?: "1" )
        inputName.setText( bun.getString("name") ?: "polaris" )
        inputProd.setText( bun.getString("prod") ?: "htc")
        inputPrice.setText( bun.getString("price") ?: "19.99" )
        itemID.setText( "ID: " + (bun.getString("item") ?: "<new item>") )
        unshowBusy()

        id = (bun.getString("item") ?: "-1").toInt()

        cancel.setOnClickListener{ this.finish() }
        save.setOnClickListener{
            showBusy()
            sendToServer()
        }
    }

    override fun publishResult(data: Request.RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Edit, Request.Signal.Add -> {
                if(data != null) {
                    if(data.resultCode >= 200 && data.resultCode < 300)
                        runOnUiThread {
                            unshowBusy()
                            finish()
                        }
                    else {
                        runOnUiThread {
                            unshowBusy()
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

    private fun sendToServer() {
        if(id <= 0) {
            Request(Constants.SERVER_DEST+"/", "POST", "", mapOf("Content-Type" to "application/json", Constants.TOKEN_HEADER to Constants.TEST_TOKEN), this, Request.Signal.Add).execute()
        } else {
            Request(Constants.SERVER_DEST+"/", "PUT", "", mapOf("Content-Type" to "application/json", Constants.TOKEN_HEADER to Constants.TEST_TOKEN), this, Request.Signal.Edit).execute()
        }
    }
}
