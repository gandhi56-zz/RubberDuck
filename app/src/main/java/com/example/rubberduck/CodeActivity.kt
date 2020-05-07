package com.example.rubberduck

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_code.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class CodeActivity : AppCompatActivity() {

    var user: User? = null
    var problemSet = ArrayList<Problem>()
    private lateinit var progBar: ProgressBar
    private lateinit var codeBtn: Button
    private lateinit var probLayout: LinearLayout
    private lateinit var probName: TextView
    private lateinit var probContent: TextView
    private lateinit var nextBtn: Button
    private lateinit var endBtn: Button
    private var pIdx: Int = 0
    private var minRating: Int = 1000
    private var inPond = false

    fun String.sendHTTPRequest(): String{
        val client = OkHttpClient()
        val request = Request.Builder().url(this).build()
        val response = client.newCall(request).execute()
        return response.body()?.string().toString()
    }

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
            problemSet.sortBy { it.rating }
            ratingLowerBound(minRating)
        }
    }

    fun ratingLowerBound(boundValue: Int){
        // TODO implement binary search version
        pIdx = 0
        while (problemSet[pIdx].rating < boundValue){
            pIdx += 1
        }
    }

    fun getElapsedSeconds(): Long{
        return SystemClock.currentThreadTimeMillis() / 1000
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class RecentSubmissionRequest: AsyncTask<Context, Void, Boolean>(){
         override fun doInBackground(vararg params: Context): Boolean {
             val url = "https://codeforces.com/api/user.status?handle=${user!!.getHandle()}&from=1&count=1"
             while (true) {
                 // TODO debug
                 val currSeconds = getElapsedSeconds()
                 if (currSeconds % 10L == 0L) {
                     val jsonObj = JSONObject(url.sendHTTPRequest())
                     if (jsonObj.getString("status") != "OK") continue
                     val subId = jsonObj.getJSONArray("result").getJSONObject(0)
                         .getInt("id")
                     if (subId == user!!.lastSubmId)    continue

                     val resultArray = jsonObj.getJSONArray("result")
                     val problemId: String
                     val problemJson = resultArray.getJSONObject(0)
                         .getJSONObject("problem")
                     println("Receiving problem JSON...")
                     if (problemJson.has("contestId") and problemJson.has("index")){
                         problemId = problemJson.getString("contestId") +
                                 problemJson.getString("index")

                         // add submission #########################################################
                         val sub = Submission()

                         // set id
                         sub.id = resultArray.getJSONObject(0).getInt("id")

                         // add verdict
                         if (resultArray.getJSONObject(0).has("verdict")){
                             val verdict = resultArray.getJSONObject(0)
                                 .getString("verdict")
                             user!!.addVerdict(verdict.toString())
                             sub.verdict = verdict
                         }
                         else{
                             sub.verdict = "Running"
                         }

                         // add problem data
                         if (resultArray.getJSONObject(0).getJSONObject("problem")
                                 .has("contestId")){
                             sub.problem.contestId = resultArray.getJSONObject(0)
                                 .getJSONObject("problem")
                                 .getInt("contestId")
                         }
                         sub.problem.index = resultArray.getJSONObject(0)
                             .getJSONObject("problem").getString("index")
                         sub.problem.name = resultArray.getJSONObject(0)
                             .getJSONObject("problem").getString("name")
                         if (resultArray.getJSONObject(0).getJSONObject("problem")
                                 .has("rating")){
                             sub.problem.rating = resultArray.getJSONObject(0)
                                 .getJSONObject("problem")
                                 .getInt("rating")
                         }
                         val tags = resultArray.getJSONObject(0)
                             .getJSONObject("problem")
                             .getJSONArray("tags")
                         (0 until tags.length()).forEach{j ->
                             sub.problem.tags.add(tags[j].toString())
                             user!!.addClass(tags[j].toString())
                         }
                         if (constantVerdict(sub.verdict)) {
                             user!!.addSubmission(problemId, sub)
                         }
                         searchProblem(problemId)
                         break
                     }
                     else{
                         println("Error 101")
                     }
                     println("done")
                 }
             }
             return true
         }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            displayProblem()
        }
    }

    private fun constantVerdict(verdict: String): Boolean {
        for (v in arrayOf("OK", "PARTIAL", "COMPILATION_ERROR", "RUNTIME_ERROR", "WRONG_ANSWER",
            "PRESENTATION_ERROR", "TIME_LIMIT_EXCEEDED", "MEMORY_LIMIT_EXCEEDED")){
            if (verdict == v)
                return true
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User
        getUIComponents()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            timer_view.isCountDown = false
        }

        timer_view.setOnChronometerTickListener {
            val time = SystemClock.elapsedRealtime() - timer_view.base
            val hrs = time / 3600000
            val mins = (time - hrs*3600000)/60000
            val sec = (time - hrs*3600000- mins*60000)/1000
            timer_view.text =
                (if (hrs < 10) "0$hrs" else hrs).toString() + ":" +
                        (if (mins < 10) "0$mins" else mins) +
                        ":" + if (sec < 10) "0$sec" else sec
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minRating = 1200
        }
        hideAll()

        ProblemsetRequest().execute()
    }

    private fun hideAll(){
        probLayout.visibility = View.INVISIBLE
        codeBtn.visibility = View.INVISIBLE
        progBar.visibility = View.INVISIBLE
        nextBtn.visibility = View.INVISIBLE
    }

    private fun getUIComponents(){
        progBar = findViewById(R.id.loadingProblems)
        codeBtn = findViewById(R.id.beginBtn)
        probLayout = findViewById(R.id.problem_layout)
        nextBtn = findViewById(R.id.next_btn)
        probName = findViewById(R.id.problem_name)
        probContent = findViewById(R.id.problem_content)
        endBtn = findViewById(R.id.end_btn)
    }

    @SuppressLint("SetTextI18n")
    fun beginCoding(@Suppress("UNUSED_PARAMETER")view: View) {
        codeBtn.visibility = View.GONE
        probLayout.visibility = View.VISIBLE
        nextBtn.visibility = View.VISIBLE
        timer_view.base = SystemClock.elapsedRealtime()
        timer_view.start()

        displayProblem()
//        FIXME
//        RecentSubmissionRequest().execute()
        inPond = true
        searchProblem("1348B")
    }

    @SuppressLint("SetTextI18n")
    fun displayProblem(){
        // TODO erase difficulty, not required to display
        println("display problem at pIdx = $pIdx")
        probName.text = problemSet[pIdx].name
        probContent.text = "ID: " + problemSet[pIdx].contestId.toString() + problemSet[pIdx].index +
                "\nDifficulty: " + problemSet[pIdx].rating.toString()
        createTable()
    }

    // onClick event handler for next problem button
    fun getProblem(@Suppress("UNUSED_PARAMETER")view: View) {
        pIdx += 2
        pIdx %= problemSet.size
        displayProblem()
    }

    // onClick event handler for end game button
    fun endGame(@Suppress("UNUSED_PARAMETER")view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure about ending this session?")
        builder.setPositiveButton("Yes"){
                _: DialogInterface?, _: Int ->
            inPond = false
            this.finish()
        }

        builder.setNegativeButton("No"){
                _: DialogInterface?, _: Int ->
            Toast.makeText(applicationContext,"Stay focused, you can solve this problem!",Toast.LENGTH_LONG).show()
        }
        val alertdiag = builder.create()
        alertdiag.setCancelable(false)
        alertdiag.show()
    }

    override fun onBackPressed() {
        // do nothing here :)
    }

    @SuppressLint("SetTextI18n")
    private fun noSubmissionView(): TextView {
        val noSubmitView = TextView(this)
        noSubmitView.apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            text = "No submissions"
            textSize = 22F
            gravity = 1
        }
        return noSubmitView
    }

    private fun submissionIdView(subObj: Submission): TextView {
        val subId = TextView(this)
        subId.apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            text = subObj.id.toString()
            textSize = 16F
        }
        return subId
    }

    private fun verdictView(subObj: Submission): TextView{
        val verdict = TextView(this)
        verdict.apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            text = subObj.verdict
            textSize = 16F
        }
        return verdict
    }

    @SuppressLint("SetTextI18n")
    private fun createTable(){
        submissionsTable!!.removeAllViews()
        if (!user!!.subm.contains(problemSet[pIdx].getId())){
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            row.addView(noSubmissionView())
            submissionsTable!!.addView(row)
            return
        }

        for (subObj in user!!.subm[problemSet[pIdx].getId()]!!){
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            row.addView(submissionIdView(subObj))
            row.addView(verdictView(subObj))
            submissionsTable!!.addView(row)
        }
    }

    private fun searchProblem(problemId: String){
        // TODO improve time complexity
        println("searching problem $problemId")
        for (i in 0 until problemSet.size){
            if (problemSet[i].getId() == problemId){
                pIdx = i
                break
            }
        }
    }

    fun sendHttpPOST(view: View) {
        var url = URL("https://192.168.1.255:8080/800/A")

    }
}
