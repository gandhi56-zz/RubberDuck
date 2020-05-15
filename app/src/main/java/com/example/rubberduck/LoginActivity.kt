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
import androidx.annotation.RequiresApi
import com.example.rubberduck.HandleInput.EMPTY
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.max

enum class HandleInput{
    WAIT, EMPTY, INVALID, OK
}

// https://codeforces.com/api/user.status?handle=Fefer_Ivan

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {

    lateinit var progBar: ProgressBar
    lateinit var handleText: EditText
    lateinit var signInBtn: Button
    lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progBar = findViewById(R.id.progressBar)
        handleText = findViewById(R.id.handleText)
        signInBtn = findViewById(R.id.signInBtn)
        progBar.visibility = View.INVISIBLE
    }

    fun signIn(view: View) {
        user = User()
        hideKeyboard()
        if (validateInput())
            UserProfileRequest().execute()
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class UserProfileRequest : AsyncTask<Context, Void, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progBar.visibility = View.VISIBLE
            handleText.isEnabled = false
            signInBtn.isEnabled = false
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Context): Boolean {
            if (!userInfo())    return false
            if (!userStatus())  return false
            if (!userRating())  return false
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            progBar.visibility = View.GONE
            if (result){
                startMainActivity()
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

        private fun userInfo(): Boolean{
            val json = sendHTTPRequest("https://codeforces.com/api/user.info?handles="
                    + getHandle())
            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED"){
                Toast.makeText(applicationContext, "Codeforces handle not found", Toast.LENGTH_LONG).show()
                return false
            }

            // user data
            val resultArray = jsonObj.getJSONArray("result")
            user.setHandle(resultArray.getJSONObject(0).getString("handle"))
            user.setTitlePhoto("https:" + resultArray.getJSONObject(0)
                .getString("titlePhoto"))

            if (resultArray.getJSONObject(0).has("rank")){
                user.setRank(resultArray.getJSONObject(0).getString("rank"))
            }
            println("User info received")
            return true
        }

        private fun userStatus(): Boolean{
            val json = sendHTTPRequest("https://codeforces.com/api/user.status?handle="
                    + getHandle())
            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED")    return false
            val resultArray = jsonObj.getJSONArray("result")

            println("#submissions = ${resultArray.length()}")

            for (i in 0 until resultArray.length()) {
                val sub = Submission()
                sub.id = resultArray.getJSONObject(i).getInt("id")
                if (!resultArray.getJSONObject(i).getJSONObject("problem").has("contestId"))    continue
                if (!resultArray.getJSONObject(i).has("verdict"))   continue
                sub.verdict = resultArray.getJSONObject(i).getString("verdict")
                sub.problem.contestId = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getInt("contestId")
                sub.problem.index = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getString("index")
                sub.problem.name = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getString("name")
                if (resultArray.getJSONObject(i).getJSONObject("problem").has("rating")){
                    sub.problem.rating = resultArray.getJSONObject(i).getJSONObject("problem")
                        .getInt("rating")
                }
                else{
                    sub.problem.rating = 0
                }
                val tags = resultArray.getJSONObject(i).getJSONObject("problem")
                    .getJSONArray("tags")
                (0 until tags.length()).forEach{j ->
                    sub.problem.tags.add(tags[j].toString())
                    user.addClass(tags[j].toString())
                }

                user.addSubmission(sub.problem.contestId.toString() + sub.problem.index, sub)
                user.lastSubmId = max(user.lastSubmId, sub.id)
                println("submission $i added")
            }
            println("user status received")
            return true
        }

        private fun userRating(): Boolean{
            val json = sendHTTPRequest("https://codeforces.com/api/user.rating?handle="
                    + user.getHandle())
            val jsonObj = JSONObject(json)
            if (jsonObj.getString("status") == "FAILED")    return false
            val resultArray = jsonObj.getJSONArray("result")
            (0 until resultArray.length()).forEach {i ->
                resultArray.getJSONObject(i)
                val ratingChangeObj = RatingChange()
                ratingChangeObj.contestId = resultArray.getJSONObject(i).getInt("contestId")
                ratingChangeObj.contestName = resultArray.getJSONObject(i).getString("contestName")
                ratingChangeObj.newRating = resultArray.getJSONObject(i).getInt("newRating")
                ratingChangeObj.rank = resultArray.getJSONObject(i).getInt("rank")
                user.ratingChangeList.add(ratingChangeObj)
            }
            return true
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
        val view: View? = this.currentFocus
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
//            intent.putExtra(Intent.EXTRA_STREAM, problemSet)
        }
        startActivity(intent)
    }
}
