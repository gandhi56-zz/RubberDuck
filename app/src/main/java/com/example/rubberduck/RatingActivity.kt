package com.example.rubberduck

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginTop
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_rating.*
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class RatingActivity : AppCompatActivity() {

    var user: User? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        val graphView = findViewById<GraphView>(R.id.RatingChart)
        val series = LineGraphSeries<DataPoint>()

        series.appendData(DataPoint(0.0, 1500.0), true, 40)
        (0 until user!!.ratingChangeList.size).forEach{ i ->
            series.appendData(
                DataPoint((i+1).toDouble(), user!!.ratingChangeList[i].newRating.toDouble()),
                true,
                40)
        }

        graphView.addSeries(series)
        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true

        createTable()
    }

    private fun createTable(){
        for (i in user!!.ratingChangeList.size-1 downTo 0){
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
                text = user!!.ratingChangeList[i].contestName
                textSize = 16F
            }
            row.addView(keyTxt)

            // add rank
            val valueTxt = TextView(this)
            valueTxt.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = user!!.ratingChangeList[i].rank.toString()
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
                text = user!!.ratingChangeList[i].newRating.toString()
                textSize = 16F
            }
            ratingTxt.gravity = 1
            row.addView(ratingTxt)

            contestsTable!!.addView(row)
        }
    }
}
