package com.example.rubberduck

import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.core.view.size
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_submission.*

class SubmissionActivity : AppCompatActivity(), OnChartValueSelectedListener {

    var user: User? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User
        piechart.description.isEnabled = false
        piechart.legend.isEnabled = false
        piechart.holeRadius = 0F
        piechart.transparentCircleRadius = 0F
        piechart.setDrawEntryLabels(false)

        // add data
        val values = ArrayList<PieEntry>()
        for ((key, value) in user!!.verdictStats){
            values.add(PieEntry(value.toFloat(), key))
        }

        val dataset = PieDataSet(values, "Verdicts")
        val pieData = PieData(dataset)
        piechart.data = pieData
        dataset.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        piechart.animateXY(1400, 1400)
        piechart.setOnChartValueSelectedListener(this)

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
            row.background = ContextCompat.getDrawable(this, R.drawable.ground_btn)
            verdictTable.addView(row)
        }
    }

    override fun onNothingSelected() {
        println("nothing selected")
        for (i in 0 until user!!.verdictStats.size){
            verdictTable[i].background = ContextCompat.getDrawable(this, R.drawable.ground_btn)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        println("Selected ${h!!.x}")
        for (i in 0 until user!!.verdictStats.size){
            if (i == h.x.toInt()){
                verdictTable[i].background = ContextCompat.getDrawable(this, R.drawable.selected_btn)
            }
            else{
                verdictTable[i].background = ContextCompat.getDrawable(this, R.drawable.ground_btn)
            }
        }
    }
}