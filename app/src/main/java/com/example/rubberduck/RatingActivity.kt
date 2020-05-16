package com.example.rubberduck

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.marginTop
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_code.*
import kotlinx.android.synthetic.main.activity_rating.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class RatingActivity : AppCompatActivity() {

    lateinit var user: User

    @SuppressLint("StaticFieldLeak")
    internal inner class UpdateRating : AsyncTask<Context, Void, Boolean>(){
        override fun onPreExecute() {
            super.onPreExecute()
            user.ratingChangeList.clear()
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Context): Boolean {
            if (!userRating())  return false
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            updateRating.isRefreshing = false
            drawChart()
            createTable()
        }

        private fun sendHTTPRequest(url: String): String{
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            return response.body()?.string().toString()
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User
        UpdateRating().execute()
        updateRating.setOnRefreshListener {
            UpdateRating().execute()
        }
    }

    private fun drawChart(){
        val series = LineGraphSeries<DataPoint>()
        series.appendData(DataPoint(0.0, 1500.0), true, 100)
        for (i in 0 until user.ratingChangeList.size){
            series.appendData(
                DataPoint((i+1).toDouble(), user.ratingChangeList[i].newRating.toDouble()),
                true,
                100)
            println(i+1)
        }
        RatingChart.viewport.isScalable = true
        RatingChart.viewport.setMaxX((user.ratingChangeList.size+2).toDouble())
        RatingChart.addSeries(series)
    }

    private fun createTable(){
        contestsTable.removeAllViews()
        for (i in user.ratingChangeList.size-1 downTo 0){
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            row.setPadding(0, 20, 0, 20)


            // add contest name
            val keyTxt = TextView(this)
            keyTxt.apply {
                layoutParams = TableRow.LayoutParams(
                    50,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = user.ratingChangeList[i].contestName
                textSize = 16F
            }
            row.addView(keyTxt)

            // add rank
            val valueTxt = TextView(this)
            valueTxt.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = user.ratingChangeList[i].rank.toString()
                textSize = 16F
            }
            valueTxt.gravity = 1
            row.addView(valueTxt)

            // add new rating
            val ratingTxt = TextView(this)
            ratingTxt.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = user.ratingChangeList[i].newRating.toString()
                textSize = 16F
            }
            ratingTxt.gravity = 1
            row.addView(ratingTxt)

            contestsTable!!.addView(row)
        }
    }
}