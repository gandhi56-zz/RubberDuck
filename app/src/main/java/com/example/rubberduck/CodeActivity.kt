package com.example.rubberduck

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_code.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Integer.max

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
            pIdx = 0
            while (problemSet[pIdx].rating < minRating){
                pIdx += 1
            }

            println("SIZE = ${problemSet.size}")
        }

    }

    @SuppressLint("StaticFieldLeak")
    internal inner class RecentSubmissionRequest: AsyncTask<Context, Void, Boolean>(){
        override fun doInBackground(vararg params: Context?): Boolean {
            val url = "https://codeforces.com/api/user.status?handle=${user!!.getHandle()}&from=1&count=1"
            val jsonObj = JSONObject(url.sendHTTPRequest())
            if (jsonObj.getString("status") == "FAILED")    return false
            val submissions = jsonObj.getJSONArray("result")

            if (submissions.getJSONObject(0).getInt("id") != user!!.lastSubmId){
                // new submission was made
                val prob = submissions.getJSONObject(0).getJSONObject("problem")
                println("last problem solved was ${prob.getString("name")}")
            }
            else{
                println("no new submission was made")
            }

            println("SUBMIT = ${submissions[0]}")

            return true
        }

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        progBar = findViewById(R.id.loadingProblems)
        codeBtn = findViewById(R.id.beginBtn)
        probLayout = findViewById(R.id.problem_layout)
        nextBtn = findViewById(R.id.next_btn)
        probName = findViewById(R.id.problem_name)
        probContent = findViewById(R.id.problem_content)
        endBtn = findViewById(R.id.end_btn)

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
        println("MIN RATING SET TO $minRating")

        probLayout.visibility = View.INVISIBLE
        codeBtn.visibility = View.INVISIBLE
        progBar.visibility = View.INVISIBLE
        nextBtn.visibility = View.INVISIBLE
        ProblemsetRequest().execute()
    }

    fun beginCoding(view: View) {
        displayProblem()
        codeBtn.visibility = View.GONE
        probLayout.visibility = View.VISIBLE
        nextBtn.visibility = View.VISIBLE
        timer_view.base = SystemClock.elapsedRealtime()
        timer_view.start()
    }

    @SuppressLint("SetTextI18n")
    fun displayProblem(){
        probName.text = problemSet[pIdx].name
        probContent.text = "ID: " + problemSet[pIdx].contestId.toString() + problemSet[pIdx].index +
                "\nDifficulty: " + problemSet[pIdx].rating.toString()
    }

    fun getProblem(view: View) {
        pIdx += 2
        pIdx %= problemSet.size
        displayProblem()
        createTable()
    }

    fun endGame(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure about ending this session?")
        builder.setPositiveButton("Yes"){
                _: DialogInterface?, _: Int ->
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

    private fun createTable(){
        submissionsTable!!.removeAllViews()
        if (!user!!.subm.contains(problemSet[pIdx].getId())){
            return
        }

        for (subObj in user!!.subm[problemSet[pIdx].getId()]!!){
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            // add key
            val subId = TextView(this)
            subId.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = subObj.id.toString()
                textSize = 16F
            }
            row.addView(subId)

            val verdict = TextView(this)
            verdict.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = subObj.verdict
                textSize = 16F
            }
            row.addView(verdict)

            submissionsTable!!.addView(row)
        }
    }

}

// TODO upon exit
class EndSessionDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.ask_exit)
                .setPositiveButton(R.string.ans_no,
                    DialogInterface.OnClickListener { dialog, id ->
                        println("Hit NO")
                    })
                .setNegativeButton(R.string.ans_yes,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                        println("Hit YES")
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

