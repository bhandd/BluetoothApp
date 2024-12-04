package com.mobila.bluetoothapp.model.utils

import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.Entry

import com.github.mikephil.charting.data.LineDataSet
import com.mobila.bluetoothapp.model.SensorData

class GraphUtils {

    fun createLineData(data: List<SensorData>): LineData {
        val xEntries = data.mapIndexed { index, point -> Entry(index.toFloat(), point.x) }
        val yEntries = data.mapIndexed { index, point -> Entry(index.toFloat(), point.y) }
        val zEntries = data.mapIndexed { index, point -> Entry(index.toFloat(), point.z) }

        val xDataSet = LineDataSet(xEntries, "X-Axis").apply {
            color = android.graphics.Color.RED
            valueTextColor = android.graphics.Color.RED
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }
        val yDataSet = LineDataSet(yEntries, "Y-Axis").apply {
            color = android.graphics.Color.BLUE
            valueTextColor = android.graphics.Color.BLUE
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }
        val zDataSet = LineDataSet(zEntries, "Z-Axis").apply {
            color = android.graphics.Color.GREEN
            valueTextColor = android.graphics.Color.GREEN
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
        }
        return LineData(xDataSet, yDataSet, zDataSet)
    }
}