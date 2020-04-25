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
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.random.Random

class CodeActivity : AppCompatActivity() {

    var problemSet = ArrayList<Problem>()
    private lateinit var progBar: ProgressBar
    private lateinit var codeBtn: Button
    private lateinit var probLayout: LinearLayout
    private lateinit var probName: TextView
    private lateinit var probContent: TextView
    private lateinit var skipBtn: Button
    private lateinit var submitBtn: Button
    private var pIdx: Int = 0

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

            problemSet.sortedWith(compareBy {it.rating})

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
        probName = findViewById(R.id.problem_name)
        probContent = findViewById(R.id.problem_content)

        probLayout.visibility = View.INVISIBLE
        codeBtn.visibility = View.INVISIBLE
        progBar.visibility = View.INVISIBLE
        skipBtn.visibility = View.INVISIBLE
        submitBtn.visibility = View.INVISIBLE
        ProblemsetRequest().execute()
    }

    fun beginCoding(view: View) {
        displayProblem(pIdx)
        codeBtn.visibility = View.GONE
        probLayout.visibility = View.VISIBLE
        skipBtn.visibility = View.VISIBLE
        submitBtn.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun displayProblem(idx: Int){
        probName.text = problemSet[idx].name
        probContent.text = "ID: " + problemSet[idx].contestId.toString() +
                "\nRating: " + problemSet[idx].rating.toString()
    }

    fun skipProblem(view: View) {
        pIdx += 1
        displayProblem(pIdx)
    }

}
