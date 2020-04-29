package com.example.rubberduck

import android.content.Intent
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
import com.github.mikephil.charting.utils.ColorTemplate

class CategoriesActivity : AppCompatActivity() {

    var user: User? = null
    var catTable: TableLayout? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        val pieChart = findViewById<PieChart>(R.id.piechart)
        catTable = findViewById(R.id.catTable)

        // add data
        val values = ArrayList<PieEntry>()
        for ((key, value) in user!!.classStats){
            values.add(PieEntry(value.toFloat(), key))
        }

        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawCenterText(false)
        pieChart.setDrawEntryLabels(false)
        pieChart.setDrawMarkers(false)

        val dataset = PieDataSet(values, "Problem categories")
        val pieData = PieData(dataset)
        pieChart.data = pieData

        dataset.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        pieChart.animateXY(1400, 1400)

        createTable()
    }

    private fun createTable(){

        for ((key, value) in user!!.classStats){
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
            catTable!!.addView(row)
        }
    }

}









