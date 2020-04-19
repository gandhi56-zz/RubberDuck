package com.example.rubberduck

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_chart.view.*
import kotlinx.android.synthetic.main.fragment_chart.view.piechart

/**
 * A simple [Fragment] subclass.
 */
class ChartFragment : Fragment() {

    private var pieChart: PieChart? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        pieChart = view.findViewById<PieChart>(R.id.piechart)

//        val data = HashMap<String, Int>()
//        data["OK"] = 120
//        data["WRONG_ANSWER"] = 200
//        data["TIME_LIMIT_EXCEEDED"] = 150

        val pieEntries = ArrayList<PieEntry>()
        val pieColors = ArrayList<Int>()
        pieEntries.add(PieEntry(20F, "OK"))
        pieEntries.add(PieEntry(50F, "WRONG_ANSWER"))
        pieEntries.add(PieEntry(30F, "TIME_LIMIT_EXCEEDED"))

        val dataset = PieDataSet(pieEntries, "Verdicts")
        dataset.setDrawIcons(false)
        dataset.sliceSpace = 3f
        dataset.iconsOffset = MPPointF(0f, 40f)
        dataset.selectionShift = 5f
        dataset.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataset)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        piechart.data = data
        piechart.highlightValues(null)
        piechart.invalidate()
        piechart.animateXY(5000,5000)

        return view
    }

}
