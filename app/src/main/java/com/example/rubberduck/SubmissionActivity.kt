package com.example.rubberduck

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.get
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate

class SubmissionActivity : AppCompatActivity(), OnChartValueSelectedListener {

    var user: User? = null
    var verdictTable: TableLayout? = null
    var pieChart: PieChart? = null
    var lastKey = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        pieChart = findViewById<PieChart>(R.id.piechart)
        verdictTable = findViewById(R.id.verdictTable)
        pieChart!!.description.isEnabled = false
        pieChart!!.legend.isEnabled = false
        pieChart!!.setDrawEntryLabels(false)
        pieChart!!.holeRadius = 0F
        pieChart!!.transparentCircleRadius = 0F
        pieChart!!.setCenterTextSize(40F)

        // add data
        val values = ArrayList<PieEntry>()
        for ((key, value) in user!!.verdictStats){
            values.add(PieEntry(value.toFloat(), key))
        }

        val dataset = PieDataSet(values, "Verdicts")
        val pieData = PieData(dataset)
        pieChart!!.data = pieData
        dataset.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        pieChart!!.animateXY(1400, 1400)
        pieChart!!.setOnChartValueSelectedListener(this)

        createTable()
//        selectRow(0)

    }

    private fun createTable(){
        var rowIdx = 0
        for ((key, value) in user!!.verdictStats){
            val row = TableRow(this)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            row.id = rowIdx
            rowIdx++

            // add key
            val keyTxt = TextView(this)
            keyTxt.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = key
                textSize = 16F
            }
            row.addView(keyTxt)

            // add value
            val valueTxt = TextView(this)
            valueTxt.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = value.toString()
                textSize = 22F
            }
            row.addView(valueTxt)
            verdictTable!!.addView(row)
        }
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }

    // TODO
    override fun onValueSelected(e: Entry?, h: Highlight?){
//        if (e != null) {
//            val key = pieChart!!.data.getDataSetForEntry(e).getEntryIndex(e as PieEntry?)
//            println("FOOOOOOOOO")
//            println(key)
//            if (key != lastKey){
//                verdictTable!!.getChildAt(lastKey).setBackgroundColor(Color.WHITE)
//                verdictTable!!.getChildAt(key).setBackgroundColor(Color.YELLOW)
//                lastKey = key
//            }
//        }

    }

    private fun selectRow(idx: Int){
        verdictTable!!.getChildAt(idx).setBackgroundColor(Color.YELLOW)
    }
}