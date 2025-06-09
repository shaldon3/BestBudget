package com.st10254797.smartbudgetting

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

class GraphActivity : AppCompatActivity() {

    private lateinit var combinedChart: CombinedChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        combinedChart = findViewById(R.id.combinedChart)

        // Sample Data (replace with your Firestore/Firebase data)
        val categoryTotals = mapOf(
            "Food" to 300f,
            "Transport" to 150f,
            "Entertainment" to 200f,
            "Utilities" to 250f
        )

        val minGoal = 100f
        val maxGoal = 400f

        setupChart(combinedChart, categoryTotals, minGoal, maxGoal)
    }

    private fun setupChart(
        chart: CombinedChart,
        categoryTotals: Map<String, Float>,
        minGoal: Float,
        maxGoal: Float
    ) {
        // Create bar entries
        val entries = categoryTotals.entries.mapIndexed { index, (category, value) ->
            BarEntry(index.toFloat(), value)
        }

        val barDataSet = BarDataSet(entries, "Spent per Category").apply {
            color = getColor(R.color.purple_700)
            valueTextSize = 12f
        }

        // Goal lines
        val yAxis = chart.axisLeft
        yAxis.removeAllLimitLines()
        yAxis.addLimitLine(LimitLine(minGoal, "Min Goal").apply {
            lineColor = Color.GREEN
            lineWidth = 2f
            textColor = Color.GREEN
        })
        yAxis.addLimitLine(LimitLine(maxGoal, "Max Goal").apply {
            lineColor = Color.RED
            lineWidth = 2f
            textColor = Color.RED
        })

        // X-axis formatter
        chart.xAxis.apply {
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return categoryTotals.keys.toList().getOrNull(value.toInt()) ?: ""
                }
            }
        }

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = true

        chart.data = CombinedData().apply {
            setData(BarData(barDataSet))
        }

        chart.invalidate()


    }
}
