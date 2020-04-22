package com.example.rubberduck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class SubmissionActivity : AppCompatActivity() {

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        val pieChart = findViewById<PieChart>(R.id.piechart)
        val desc = Description()
        desc.text = "Submissions statistics"
        desc.textSize = 16f
        pieChart.description = desc

        // add data
        val values = ArrayList<PieEntry>()
        for ((key, value) in user!!.verdictStats){
            values.add(PieEntry(value.toFloat(), key))
        }

        val dataset = PieDataSet(values, "Verdicts")
        val pieData = PieData(dataset)
        pieChart.data = pieData

        dataset.colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
        pieChart.animateXY(1400, 1400)
    }
}
