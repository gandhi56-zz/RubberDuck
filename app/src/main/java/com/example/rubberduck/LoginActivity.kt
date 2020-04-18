package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request


const val EMAIL_MESSAGE = "com.example.rubberduck.EMAIL_MESSAGE"

// https://codeforces.com/api/user.info?handles=DmitriyH;Fefer_Ivan
// https://codeforces.com/api/user.info?handles=gandhi56

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {

    lateinit var progBar: ProgressBar
    val tag: String = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progBar = findViewById<ProgressBar>(R.id.progressBar)
        progBar.setVisibility(View.INVISIBLE)
    }

    fun startMainActivity(view: View){
        if (validateInput()){
            getUserProfile().execute()
            println("done")
        }
        else{

        }
    }

    internal inner class getUserProfile : AsyncTask<Void, Void, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progBar.setVisibility(View.VISIBLE)
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Void?): String {
            val client = OkHttpClient()
            val url = "https://codeforces.com/api/user.info?handles=" + getHandle()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val json = response.body()?.string().toString()
            val lbl = findViewById<TextView>(R.id.textView2)
            lbl.text = json
            return json
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progBar.setVisibility(View.GONE)
        }
    }

    private fun validateInput(): Boolean{
        return true
    }

    private fun getHandle(): String{
        val nameTxt = findViewById<EditText>(R.id.handleText)
        return nameTxt.text.toString()
    }

}
