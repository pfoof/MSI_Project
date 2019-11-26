package com.example.msiproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.msiproject.utils.Constants
import com.example.msiproject.utils.Request
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.net.URI
import java.net.URL

class LoginActivity : AppCompatActivity(), Request.IRequestResult {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(intent != null && intent.data != null) {
            val d = intent.data
            if(d?.path != null) {
                val path = d?.path
                if(path!!.isNotEmpty())
                    login(path)
            }
        }

        github.setOnClickListener{
            val url = Uri.parse(Constants.SERVER_DEST+"/login/github")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        local.setOnClickListener {
            val url = Uri.parse(Constants.SERVER_DEST+"/login/local")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        if(hasToken()) {
            validateToken(getToken()!!)
        }

    }

    private fun continueToMain(offline: Boolean) {
        runOnUiThread {
            val intent = Intent(this, MainActivity::class.java)
            if(offline) intent.putExtra("offline", true)
            startActivity(intent)
            finish()
        }
    }

    private fun invalidateToken() {
        val ed = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE).edit()
        ed.remove(Constants.TOKEN_HEADER)
        ed.apply()
    }

    private fun showError(e: String) {
        runOnUiThread {
            err.visibility = View.VISIBLE
            err.text = e
        }
    }

    private fun showOffline() {
        runOnUiThread {
            offline.visibility = View.VISIBLE
            offline.setOnClickListener {
                continueToMain(true)
            }
        }
    }

    override fun publishResult(data: Request.RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Authorize -> {
                if(data == null || data.resultCode >= 400) {
                    invalidateToken()
                    showError("Please login again")
                } else if(data.resultCode >= 200 && data.resultCode < 250) {
                    continueToMain(false)
                }
            }
        }
    }

    override fun publishProblem(e: Exception?) {
        runOnUiThread { Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show() }
        showOffline()
    }

    private fun hasToken(): Boolean {
        val prefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE)
        return prefs.contains(Constants.TOKEN_HEADER)
    }

    private fun getToken(): String? {
        val prefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE)
        return prefs.getString(Constants.TOKEN_HEADER, "xxx")
    }

    private fun validateToken(token: String) {
        Request(Constants.SERVER_DEST+"/authorize", "GET", "", mapOf(Constants.TOKEN_HEADER to token), this, Request.Signal.Authorize).execute()
    }

    private fun login(token: String) {

    }
}
