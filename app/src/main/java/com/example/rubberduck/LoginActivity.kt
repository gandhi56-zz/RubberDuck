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
import android.widget.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

enum class HandleInput{
    WAIT, EMPTY, INVALID, OK
}

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {

    lateinit var progBar: ProgressBar
    lateinit var handleText: EditText
    lateinit var lbl: TextView
    lateinit var signInBtn: Button
    val tag: String = "LoginActivity"
    var handleState: HandleInput = HandleInput.EMPTY
    var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progBar = findViewById<ProgressBar>(R.id.progressBar)
        handleText = findViewById<EditText>(R.id.handleText)
        lbl = findViewById(R.id.textView2)
        signInBtn = findViewById<Button>(R.id.signInBtn)

        progBar.setVisibility(View.INVISIBLE)
    }

    fun signIn(view: View){
        handleState = HandleInput.WAIT
        hide_keyboard()
        if (validateInput())
            getUserProfile().execute()
        else
            handleState = HandleInput.EMPTY

        // FIXME async toasts
        when (handleState){
            HandleInput.EMPTY -> {
                Toast.makeText(this,"Please enter a codeforces handle",Toast.LENGTH_SHORT).show()
            }
            HandleInput.INVALID ->{
                Toast.makeText(this,"Invalid codeforces handle",Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal inner class getUserProfile : AsyncTask<Context, Void, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progBar.setVisibility(View.VISIBLE)
            handleText.setEnabled(false)
            signInBtn.setEnabled(false)
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Context): Boolean {
            val client = OkHttpClient()
            val url = "https://codeforces.com/api/user.info?handles=" + getHandle()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val json = response.body()?.string().toString()
            lbl.text = json

            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED"){
                return false
            }

            val resultArray = jsonObj.getJSONArray("result")
            user.setHandle(resultArray.getJSONObject(0).getString("handle"))
            user.setTitlePhoto("https:" + resultArray.getJSONObject(0).getString("titlePhoto"))
            user.setRank(resultArray.getJSONObject(0).getString("rank"))
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            progBar.setVisibility(View.GONE)
            if (result){
                handleState = HandleInput.OK
                startMainActivity()
            }
            else{
                handleState = HandleInput.INVALID
            }
            handleText.setEnabled(true)
            signInBtn.setEnabled(true)
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

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Intent.EXTRA_USER, user)
        startActivity(intent)
    }
}
