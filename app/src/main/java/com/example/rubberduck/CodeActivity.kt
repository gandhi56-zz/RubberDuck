package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class CodeActivity : AppCompatActivity() {

    var problemSet = ArrayList<Problem>()
    lateinit var progBar: ProgressBar
    lateinit var codeBtn: Button
    lateinit var probLayout: LinearLayout
    lateinit var skipBtn: Button
    lateinit var submitBtn: Button

    @SuppressLint("StaticFieldLeak")
    internal inner class ProblemsetRequest: AsyncTask<Context, Void, Boolean>(){

        override fun onPreExecute() {
            super.onPreExecute()
            progBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Context?): Boolean {
            val jsonObj = JSONObject(
                " https://codeforces.com/api/problemset.problems".sendHTTPRequest()
            )
            if (jsonObj.getString("status") == "FAILED")    return false
            val problemsetJson = jsonObj.getJSONObject("result")
                .getJSONArray("problems")

            (0 until problemsetJson.length()).forEach{ i->
                val prob = Problem()
                val probJson = problemsetJson.getJSONObject(i)
                if (probJson.has("contestId")){
                    prob.contestId = probJson.getInt("contestId")
                }
                prob.index = probJson.getString("index")
                prob.name = probJson.getString("name")
                if (probJson.has("rating")){
                    prob.rating = probJson.getInt("rating")
                }
                val tags = probJson.getJSONArray("tags")
                (0 until tags.length()).forEach{j ->
                    prob.tags.add(tags[j].toString())
                }
                problemSet.add(prob)
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progBar.visibility = View.GONE
            codeBtn.visibility = View.VISIBLE
            println("SIZE = ${problemSet.size}")
        }

        private fun String.sendHTTPRequest(): String{
            val client = OkHttpClient()
            val request = Request.Builder().url(this).build()
            val response = client.newCall(request).execute()
            return response.body()?.string().toString()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        progBar = findViewById(R.id.loadingProblems)
        codeBtn = findViewById(R.id.beginBtn)
        probLayout = findViewById(R.id.problem_layout)
        skipBtn = findViewById(R.id.skip_btn)
        submitBtn = findViewById(R.id.submit_btn)
        probLayout.visibility = View.INVISIBLE
        codeBtn.visibility = View.INVISIBLE
        progBar.visibility = View.INVISIBLE
        skipBtn.visibility = View.INVISIBLE
        submitBtn.visibility = View.INVISIBLE
        ProblemsetRequest().execute()
    }

    fun beginCoding(view: View) {
        codeBtn.visibility = View.GONE
        probLayout.visibility = View.VISIBLE
        skipBtn.visibility = View.VISIBLE
        submitBtn.visibility = View.VISIBLE
    }


}
