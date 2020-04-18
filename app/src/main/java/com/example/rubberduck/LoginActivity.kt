package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
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

// https://codeforces.com/api/user.status?handle=Fefer_Ivan

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {

    lateinit var progBar: ProgressBar
    lateinit var handleText: EditText
    lateinit var lbl: TextView
    lateinit var signInBtn: Button
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

    fun signIn(view: View) {
        handleState = HandleInput.WAIT
        hideKeyboard()
        if (validateInput())
            UserProfileRequest().execute()
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

    @SuppressLint("StaticFieldLeak")
    internal inner class UserProfileRequest : AsyncTask<Context, Void, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progBar.visibility = View.VISIBLE
            handleText.isEnabled = false
            signInBtn.isEnabled = false
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Context): Boolean {
            var json = sendHTTPRequest("https://codeforces.com/api/user.info?handles=" + getHandle())
            var jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED")    return false

            // user data
            var resultArray = jsonObj.getJSONArray("result")
            user.setHandle(resultArray.getJSONObject(0).getString("handle"))
            user.setTitlePhoto("https:" + resultArray.getJSONObject(0).getString("titlePhoto"))
            user.setRank(resultArray.getJSONObject(0).getString("rank"))

            // submissions of the user
            json = sendHTTPRequest("https://codeforces.com/api/user.status?handle=" + getHandle())
            jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED")    return false
            lbl.text = json
            resultArray = jsonObj.getJSONArray("result")
            println("LEN = ${resultArray.length()}")
            println("RESULT = ${resultArray.getJSONObject(0).getJSONObject("problem")}")

            (0 until resultArray.length()-1).forEach { i ->
                val sub = Submission()
                sub.id = resultArray.getJSONObject(i).getInt("id")
                sub.verdict = resultArray.getJSONObject(i).getString("verdict")

                sub.problem.contestId = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getInt("contestId")
                sub.problem.index = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getString("index")
                sub.problem.name = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getString("name")
//                sub.problem.rating = resultArray.getJSONObject(i).getJSONObject("problem")
//                    .getInt("rating")

                val tags = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getJSONArray("tags")
                (0 until tags.length()-1).forEach{j ->
                    sub.problem.tags.add(tags[j].toString())
                }
                user.submissions.add(sub)
            }
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            progBar.visibility = View.GONE
            if (result){
                handleState = HandleInput.OK
                startMainActivity()
            }
            else{
                handleState = HandleInput.INVALID
            }
            handleText.isEnabled = true
            signInBtn.isEnabled = true
        }

        private fun sendHTTPRequest(url: String): String{
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            return response.body()?.string().toString()
        }
    }

    private fun validateInput(): Boolean{
        return getHandle().isNotEmpty()
    }

    private fun getHandle(): String{
        val nameTxt = findViewById<EditText>(R.id.handleText)
        return nameTxt.text.toString()
    }

    private fun hideKeyboard(){
        val view = this.currentFocus
        if (view != null){
            val hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }
}
