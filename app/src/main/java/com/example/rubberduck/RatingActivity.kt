package com.example.rubberduck

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
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

        (0 until user!!.ratingList.size).forEach{ i ->
            series.appendData(
                DataPoint(i.toDouble(), user!!.ratingList[i].toDouble()),
                true,
                40)
        }

        graphView.addSeries(series)
        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true
    }
}
