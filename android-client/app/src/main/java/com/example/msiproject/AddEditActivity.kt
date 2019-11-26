package com.example.msiproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_edit.*

class AddEditActivity : AppCompatActivity() {

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

        id = (bun.getString("item") ?: "-1").toInt()
    }
}
