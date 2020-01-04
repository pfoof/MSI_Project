package com.example.msiprojectv2.sets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.msiprojectv2.R
import com.example.msiprojectv2.local.Local
import com.example.msiprojectv2.local.SaveItemsLocallyTask
import com.example.msiprojectv2.utils.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_sets.*
import java.lang.Exception

class SetsActivity : AppCompatActivity(), Request.IRequestResult {

    private var isOffline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sets)

        setsRefresh.setOnClickListener { refreshSets() }
    }

    override fun publishResult(data: RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.FetchSet -> {
                runOnUiThread{ setsRefresh.isEnabled = true }
                if(data != null && data.resultCode < 300 && data.resultCode >= 200) {
                    val objectMapper = ObjectMapper()
                    val items: SetModelsAndSetItems = objectMapper.readValue(data.data)
                    Local.deleteAllSets(this)
                    Local.insertSetItems(this, items.items)
                    Local.insertSetModels(this, items.models)
                    val adapter = SetsAdapter(this, items.models)
                    populateList(adapter)
                }
            }
        }
    }

    private fun populateList(adapter: SetsAdapter) {
        runOnUiThread {
            setsList.adapter = adapter
        }
    }

    private fun refreshSets() {
        if(!setsRefresh.isEnabled) return
        setsRefresh.isEnabled = false

        if(isOffline) {
            LoadSetsFromDBTask(this, this).execute()
        } else
            Request(Constants.SERVER_DEST(null)+"/collection", "GET", "", mapOf(Constants.TOKEN_HEADER to Tokens.getToken(this)), this, Request.Signal.FetchSet).execute()
    }

    override fun publishProblem(e: Exception?) {
        runOnUiThread{ Toast.makeText(this, e!!.message, Toast.LENGTH_SHORT).show() }
    }
}
