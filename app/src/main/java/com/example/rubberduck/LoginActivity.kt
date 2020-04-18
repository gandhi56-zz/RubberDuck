package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


const val EMAIL_MESSAGE = "com.example.rubberduck.EMAIL_MESSAGE"

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
        hide_keyboard()
        if (validateInput()){
            getUserProfile().execute()
            println("done")
        }
        else{
            Toast.makeText(this,"Please enter a codeforces handle",Toast.LENGTH_SHORT).show()
        }
    }

    internal inner class getUserProfile : AsyncTask<Context, Void, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progBar.setVisibility(View.VISIBLE)
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Context): String {
            val client = OkHttpClient()
            val url = "https://codeforces.com/api/user.info?handles=" + getHandle()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val json = response.body()?.string().toString()
            val lbl = findViewById<TextView>(R.id.textView2)
            lbl.text = json

            val jsonObj = JSONObject(json)
            val status = jsonObj.getString("status")
            if (status == "FAILED"){
                // TODO reason("display toast that the input handle is invalid")
//                Toast.makeText(applicationContext,"Please enter a codeforces handle",Toast.LENGTH_SHORT).show()
            }
            return "foo"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progBar.setVisibility(View.GONE)
        }
    }

    private fun validateInput(): Boolean{
        return getHandle().isNotEmpty()
    }

    private fun getHandle(): String{
        val nameTxt = findViewById<EditText>(R.id.handleText)
        return nameTxt.text.toString()
    }

    private fun hide_keyboard(){
        val view = this.currentFocus
        if (view != null){
            val hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
