package com.example.rubberduck

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.activity_login.*

class CategoriesActivity : AppCompatActivity() {

    var user: User? = null
    val tableLayout by lazy{TableLayout(this)}
    var scrollview: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        user = intent.getSerializableExtra(Intent.EXTRA_USER) as User

        val pieChart = findViewById<PieChart>(R.id.piechart)
        scrollview = findViewById(R.id.tableScroll)

        val desc = Description()
        desc.text = "Categories statistics"
        desc.textSize = 20f
        pieChart.description = desc

        // add data
        val values = ArrayList<PieEntry>()
        for ((key, value) in user!!.classStats){
            values.add(PieEntry(value.toFloat(), key))
        }

        val dataset = PieDataSet(values, "Problem categories")
        val pieData = PieData(dataset)
        pieChart.data = pieData

        dataset.colors = ColorTemplate.JOYFUL_COLORS.toMutableList()
        pieChart.animateXY(1400, 1400)

        createTable()
    }

    private fun createTable(){

        tableLayout.apply{
            layout(10, 10, 0, 10)
            // TODO
//            background = Drawable.createFromPath("drawable/handle_view.xml")
        }

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
                textSize = 22F
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
            tableLayout.addView(row)
        }
        scrollview?.addView(tableLayout)
    }

}









