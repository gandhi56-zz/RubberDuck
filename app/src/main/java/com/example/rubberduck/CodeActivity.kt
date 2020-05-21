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
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_code.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.Socket
import java.net.UnknownHostException

class CodeActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    lateinit var user: User
    lateinit var socket: Socket
    lateinit var dos: DataOutputStream

    var problemSet = ArrayList<Problem>()
    var problemTitles = ArrayList<String>()
    private lateinit var progBar: ProgressBar
    private lateinit var probLayout: LinearLayout
    private lateinit var probName: TextView
    private lateinit var probContent: TextView
    private lateinit var nextBtn: Button
    private var pIdx: Int = 0
    private var leftIdx: Int = 0
    private var rightIdx: Int = 0
    private var minRating: Int = 1000
    private var maxRating: Int = 1200

    private lateinit var submissionTable: SubmissionTable

    private lateinit var adapter: ArrayAdapter<String>

    fun String.sendHTTPRequest(): String{
        val client = OkHttpClient()
        val request = Request.Builder().url(this).build()
        val response = client.newCall(request).execute()
        return response.body()?.string().toString()
    }

    // ######################################################################################################
    // submissionTable class                                                                                #
    // ######################################################################################################

    internal inner class SubmissionTable(ctx: Context) {

        private var appCtx: Context? = ctx
        @SuppressLint("SetTextI18n")
        private fun noSubmissionView(): TextView {
            val noSubmitView = TextView(appCtx)
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
            val subId = TextView(appCtx)
            subId.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = subObj.id.toString()
                textSize = 16F
            }
            return subId
        }

        private fun verdictView(subObj: Submission): TextView{
            val verdict = TextView(appCtx)
            verdict.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = subObj.verdict
                textSize = 16F
            }
            return verdict
        }

        @SuppressLint("SetTextI18n")
        fun createTable(){
            submissionsTable!!.removeAllViews()
            if (!user.subm.contains(problemSet[pIdx].getId())){
                val row = TableRow(appCtx)
                row.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                row.addView(noSubmissionView())
                submissionsTable!!.addView(row)
                return
            }

            for ((i, subObj)
            in user.subm[problemSet[pIdx].getId()]!!.sortedBy { it-> it.id }.withIndex()){
                val row = TableRow(appCtx)
                row.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                row.addView(submissionIdView(subObj))
                row.addView(verdictView(subObj))
                submissionsTable!!.addView(row)
            }
        }

    }


    // ######################################################################################################
    // problemset request asynctask                                                                         #
    // ######################################################################################################
    @SuppressLint("StaticFieldLeak")
    internal inner class ProblemsetRequest: AsyncTask<Context, Void, Boolean>(){

        override fun onPreExecute() {
            super.onPreExecute()
            progBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Context?): Boolean {
            val jsonObj = JSONObject(" https://codeforces.com/api/problemset.problems".sendHTTPRequest())
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
                problemTitles.add(prob.getTitle())
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progBar.visibility = View.GONE
            problemSet.sortBy { it.rating }
            minRating = 100*((user.ratingChangeList[user.ratingChangeList.size-1].newRating)/100)
            maxRating = minRating + 200
            leftIdx = ratingLowerBound(minRating)
            rightIdx = ratingLowerBound((maxRating))
            ratingLowerBound(minRating)
            beginCoding()
        }
    }

    // ######################################################################################################
    // recentSubmission request asynctask                                                                   #
    // ######################################################################################################

    @SuppressLint("StaticFieldLeak")
    internal inner class RecentSubmissionRequest: AsyncTask<Context, Void, Boolean>(){
        override fun doInBackground(vararg params: Context): Boolean {
            val url = "https://codeforces.com/api/user.status?handle=${user.getHandle()}&from=1&count=1"
            val jsonObj = JSONObject(url.sendHTTPRequest())
            if (jsonObj.getString("status") != "OK"){
                println("Request failed...")
                return false
            }
            val resultArray = jsonObj.getJSONArray("result")
            val problemId: String
            val problemJson = resultArray.getJSONObject(0)
                .getJSONObject("problem")
            if (!problemJson.has("contestId") or !problemJson.has("index")){
                return false
            }
            problemId = problemJson.getString("contestId") + problemJson.getString("index")
            if (!resultArray.getJSONObject(0).has("verdict")){
                return false
            }
            if (!resultArray.getJSONObject(0).getJSONObject("problem").has("contestId")){
                println("Error: Problem does not have a value for contestId tag")
                return false
            }
            val verdict = resultArray.getJSONObject(0).getString("verdict")
            if (!user.constantVerdict(verdict)){
                return false
            }

            val sub = Submission()
            sub.verdict = verdict
            sub.id = resultArray.getJSONObject(0).getInt("id")
            sub.problem.contestId = resultArray.getJSONObject(0)
                .getJSONObject("problem")
                .getInt("contestId")
            sub.problem.index = resultArray.getJSONObject(0)
                .getJSONObject("problem").getString("index")
            sub.problem.name = resultArray.getJSONObject(0)
                .getJSONObject("problem").getString("name")
            if (resultArray.getJSONObject(0).getJSONObject("problem").has("rating")){
                sub.problem.rating = resultArray.getJSONObject(0)
                    .getJSONObject("problem")
                    .getInt("rating")
            }
            val tags = resultArray.getJSONObject(0)
                .getJSONObject("problem")
                .getJSONArray("tags")
            (0 until tags.length()).forEach{j ->
                sub.problem.tags.add(tags[j].toString())
                user.addClass(tags[j].toString())
            }
            if (user.constantVerdict(sub.verdict) and isNewSubmission(problemId, sub)) {
                user.addSubmission(problemId, sub)
            }
            searchProblem(problemId)
            return true
        }

        private fun isNewSubmission(problemId: String, sub: Submission): Boolean {
//            for (subObj in user.subm[problemId]!!){
//                if (subObj.id == sub.id)
//                    return false
//            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result!!)
                displayProblem()
        }
    }

    // ######################################################################################################
    // Client socket implementation                                                                         #
    // ######################################################################################################

    @SuppressLint("StaticFieldLeak")
    internal inner class ClientSocket: AsyncTask<Context, Void, Boolean>(){

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: Context?): Boolean {
            try{
                socket = Socket("192.168.1.74", 8080)
                dos = DataOutputStream(socket.getOutputStream())
                dos.writeUTF("Iloveyou3000")
                return true
            }
            catch(e: Exception){
                Toast.makeText(applicationContext, "Unknown host exception caught", Toast.LENGTH_LONG).show()
                return false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            if (result!!)
                socket.close()
        }
    }


    // ######################################################################################################
    // Activity driver function                                                                             #
    // ######################################################################################################

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
            maxRating = 1400
        }

        nextBtn.setOnLongClickListener {
            translateRating(100)
            return@setOnLongClickListener true
        }

        hideAll()
        ProblemsetRequest().execute()
        submissionTable = SubmissionTable(applicationContext)
    }

    fun ratingLowerBound(boundValue: Int): Int{
        var lo = 0
        var hi = problemSet.size-1
        while (lo < hi){
            val mid = (lo + hi)/2
            val diff = problemSet[mid].rating
            if (diff >= boundValue)
                hi = mid
            else
                lo = mid+1
        }
        return lo
    }

    private fun hideAll(){
        probLayout.visibility = View.INVISIBLE
        progBar.visibility = View.INVISIBLE
        nextBtn.visibility = View.INVISIBLE
    }

    private fun getUIComponents(){
        progBar = findViewById(R.id.loadingProblems)
        probLayout = findViewById(R.id.problem_layout)
        nextBtn = findViewById(R.id.next_btn)
        probName = findViewById(R.id.problem_name)
        probContent = findViewById(R.id.problem_content)
    }

    fun beginCoding() {
        probLayout.visibility = View.VISIBLE
        nextBtn.visibility = View.VISIBLE
        timer_view.base = SystemClock.elapsedRealtime()
        timer_view.start()
        displayProblem()

        pullToRefresh.setOnRefreshListener {
            RecentSubmissionRequest().execute()
        }
        RecentSubmissionRequest().execute()

        adapter = ArrayAdapter(this, R.layout.problems_list, problemTitles)
        searchView.setOnQueryTextListener(this)
        searchListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val item = searchListView.getItemAtPosition(position) as String
                var probId = ""
                var i = 0
                while (i < item.length){
                    probId += item[i]
                    i++
                    if (item[i] == ' ') break
                }
                if (searchProblem(probId))
                    displayProblem()
                else
                    Toast.makeText(applicationContext, "Problem $item not found", Toast.LENGTH_LONG).show()
                searchListView.adapter = null
                hideKeyboard()
            }

    }

    fun initSearch(view: View) {
        searchListView.adapter = adapter
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
//        println("search query = $query")
//        if (searchProblem(query!!)){
//            displayProblem()
//            hideKeyboard()
//            searchView.isEnabled = false
//            searchListView.adapter = null
//            return true
//        }
//        searchListView.adapter = null
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (searchListView.adapter == null) {
            searchListView.adapter = adapter
        }
        adapter.filter.filter(newText)
        return true
    }

    @SuppressLint("SetTextI18n")
    fun displayProblem(){
        // TODO erase difficulty, not required to display
        println("display problem at pIdx = $pIdx")
        pullToRefresh.isRefreshing = false
        probName.text = problemSet[pIdx].name
        probContent.text = "ID: " + problemSet[pIdx].contestId.toString() + problemSet[pIdx].index +
                "\nDifficulty: " + problemSet[pIdx].rating.toString()
        submissionTable.createTable()
    }

    // onClick event handler for next problem button
    fun getProblem(@Suppress("UNUSED_PARAMETER")view: View) {
        pIdx = getRandomIndex()
        displayProblem()
    }

    private fun getRandomIndex(): Int {
        return (leftIdx..rightIdx).random()
    }

    private fun translateRating(delta: Int): Boolean{
        if ((rightIdx + delta >= problemSet.size) or (leftIdx - delta < 0))    return false
        leftIdx += delta
        rightIdx += delta
        minRating = problemSet[leftIdx].rating
        maxRating = problemSet[rightIdx].rating
        pIdx = leftIdx
        displayProblem()
        Toast.makeText(applicationContext, "Difficulty level has been increased", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onBackPressed() {
        if (searchListView.adapter != null){
            searchListView.adapter = null
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure about ending this session?")
        builder.setPositiveButton("Yes"){ _: DialogInterface?, _: Int ->
            run {
                this.finish()
            }
        }

        builder.setNegativeButton("No"){
                _: DialogInterface?, _: Int ->
            Toast.makeText(applicationContext,"Stay focused, you can solve this problem!",Toast.LENGTH_LONG).show()
        }
        val alert = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    @SuppressLint("ShowToast")
    private fun searchProblem(query: String?):Boolean{
        // TODO improve time complexity
        println("searching problem $query")
        for (i in 0 until problemSet.size){
            if ((problemSet[i].getId() == query) or (problemSet[i].name == query)){
                pIdx = i
                println("found problem")
                return true
            }
        }
        println("problem not found")
        return false
    }

    override fun onSearchRequested(): Boolean {
        Bundle().apply {
            putBoolean("Searching and searching...", true)
        }
        return true
    }

    private fun hideKeyboard(){
        val view: View? = this.currentFocus
        if (view != null){
            val hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun sendProblemToServer(view: View) {
        ClientSocket().execute()
    }

}