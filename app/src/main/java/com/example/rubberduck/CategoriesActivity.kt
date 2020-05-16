package com.example.rubberduck

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class CategoriesActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var catTable: TableLayout

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        val pieChart = findViewById<PieChart>(R.id.piechart)
        catTable = findViewById(R.id.catTable)

        // add data
        val values = ArrayList<PieEntry>()
        for ((key, value) in user.classStats){
            values.add(PieEntry(value.toFloat(), key))
        }

        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.holeRadius = 0F
        pieChart.transparentCircleRadius = 0F
        pieChart.setCenterTextSize(40F)

        val dataset = PieDataSet(values, "Problem categories")
        val pieData = PieData(dataset)
        pieChart.data = pieData

        val colors = arrayOf(
            Color.parseColor("#ED0A3F"),
            Color.parseColor("#FF8833"),
            Color.parseColor("#FFAE42"),
            Color.parseColor("#FED85D"),
            Color.parseColor("#AFE313"),
            Color.parseColor("#9DE093"),
            Color.parseColor("#63B76C"),
            Color.parseColor("#93CCEA"),
            Color.parseColor("#6456B7"),
            Color.parseColor("#00CCCC"),
            Color.parseColor("#C154C1"),
            Color.parseColor("#F653A6"),
            Color.parseColor("#E30B5C"),
            Color.parseColor("#FFFF66"),
            Color.parseColor("#1CAC78"),
            Color.parseColor("#EE34D2"),
            Color.parseColor("#FF9966"),
            Color.parseColor("#66FF66"),
            Color.parseColor("#93DFB8"),
            Color.parseColor("#0095B7"),
            Color.parseColor("#0066CC"),
            Color.parseColor("#652DC1"),
            Color.parseColor("#BB3385"),
            Color.parseColor("#F8FC98"),
            Color.parseColor("#00755E"),
            Color.parseColor("#6CDAE7"),
            Color.parseColor("#D6AEDD"),
            Color.parseColor("#FFB7D5")
        )

//        dataset.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        pieChart.animateXY(1400, 1400)
        dataset.colors = colors.toMutableList()

        createTable()
    }

    private fun createTable(){

        for ((key, value) in user.classStats){
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            // add key
            val keyTxt = TextView(this)
            keyTxt.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = key
                textSize = 16F
            }
            row.addView(keyTxt)

            // add value
            val valueTxt = TextView(this)
            valueTxt.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = value.toString()
                textSize = 22F
            }
            row.addView(valueTxt)
            catTable.addView(row)
        }
    }

}
