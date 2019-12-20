package com.example.msiproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.msiproject.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception

class LoginActivity : AppCompatActivity(), Request.IRequestResult {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(intent != null && intent.data != null) {
            val d = intent.data
            if(d?.path != null) {
                val path = d?.encodedQuery
                if(path!!.isNotEmpty())
                    login(path)
            }
        }

        github.setOnClickListener{
            val url = Uri.parse(Constants.SERVER_DEST(serverDest)+"/login/github")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        local.setOnClickListener {
            val url = Uri.parse(Constants.SERVER_DEST(serverDest)+"/login/local")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        if(hasToken()) {
            validateToken(getToken()!!)
        }

        err.visibility = View.GONE

    }

    private fun continueToMain(offline: Boolean) {
        runOnUiThread {
            val intent = Intent(this, MainActivity::class.java)
            if(offline) intent.putExtra("offline", true)
            startActivityForResult(intent, Constants.ACTIVITY_REQUEST_MAIN)
        }
    }

    private fun invalidateToken() {
        val ed = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE).edit()
        ed.remove(Constants.TOKEN_HEADER)
        ed.commit()
    }

    private fun showError(e: String) {
        runOnUiThread {
            err.visibility = View.VISIBLE
            err.text = e
        }
    }

    private fun showOffline() {
        runOnUiThread {
            github.isEnabled = false
            local.isEnabled = false
            offline.visibility = View.VISIBLE
            offline.setOnClickListener {
                continueToMain(true)
            }
        }
    }

    override fun publishResult(data: RequestResult?, sig: Request.Signal?) {
        when(sig) {
            Request.Signal.Authorize -> {

                if(data == null || data.resultCode < 0) {
                    showError("Connection error")
                    showOffline()
                    return
                }

                if(data.resultCode >= 400) {
                    invalidateToken()
                    showError("Please login again")
                } else if(data.resultCode >= 200 && data.resultCode < 250) {
                    Log.d("User_Token", data.data)
                    saveUserLevel(UserLevel.getInstance(data.data).getLevel())
                    continueToMain(false)
                }
            }
        }
    }

    override fun publishProblem(e: Exception?) {
        runOnUiThread { Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show() }
    }

    private fun hasToken(): Boolean = Tokens.hasToken(this)
    private fun getToken(): String? = Tokens.getToken(this)

    private fun validateToken(token: String) {
        Request(Constants.SERVER_DEST(serverDest)+"/authorize", "GET", "", mapOf(Constants.TOKEN_HEADER to token), this, Request.Signal.Authorize).execute()
    }

    private fun login(token: String) {
        val prefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE).edit()
        prefs.putString(Constants.TOKEN_HEADER, token.substringAfter("token="))
        prefs.commit()
    }

    private fun saveUserLevel(level: Int) {
        val prefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE).edit()
        prefs.putInt(Constants.USER_LEVEL, level)
        prefs.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Constants.ACTIVITY_REQUEST_MAIN && resultCode == Constants.ACTIVITY_RESULT_FAIL) {
            Toast.makeText(this, "Please login again!", Toast.LENGTH_LONG).show()
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }
}
