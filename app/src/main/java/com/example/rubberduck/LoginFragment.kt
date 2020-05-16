package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.max

class LoginFragment : Fragment() {

    val user = User()

    @SuppressLint("StaticFieldLeak")
    internal inner class UserProfileRequest : AsyncTask<Context, Void, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            usernameLogin.isEnabled = false
            passwordLogin.isEnabled = false
            loginBtn.isEnabled = false
        }

        override fun doInBackground(vararg params: Context): Boolean {
            if (!userInfo())    return false
            if (!userStatus())  return false
            if (!userRating())  return false
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result){
                startMainActivity()
            }
            else{
                println("Error logging in")
            }
            usernameLogin.isEnabled = true
            passwordLogin.isEnabled = true
            loginBtn.isEnabled = true
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
            if (jsonObj.getString("status") == "FAILED")    return false
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.loginBtn.setOnClickListener {
            loginEventHandler()
        }
        return view
    }

    private fun loginEventHandler(){
        println("Logging user in")
        val username = usernameLogin.text.toString()
        val password = passwordLogin.text.toString()
        if (valid(username, password)){
            UserProfileRequest().execute()
        }
        else{
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show()
        }
    }

    private fun valid(username: String, password: String): Boolean{
        return true
    }

    private fun getHandle(): String{
        return usernameLogin.text.toString()
    }

    private fun startMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Intent.EXTRA_USER, user)
        }
        startActivity(intent)
    }


}
