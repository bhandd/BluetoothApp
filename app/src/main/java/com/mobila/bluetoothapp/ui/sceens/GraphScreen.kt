package com.mobila.bluetoothapp.ui.sceens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mobila.bluetoothapp.ui.viewmodels.MotionViewModelBase

@Composable
fun GraphScreen (
    vm: MotionViewModelBase
) {
    val context = LocalContext.current
    val dataPoints = remember { mutableStateListOf<Entry>() }

    // Simulate dynamic data updates
    LaunchedEffect(Unit) {
        var x = 0f
        while (x < 100) {
            dataPoints.add(Entry(x, (Math.random() * 100).toFloat()))
            x += 1f
            kotlinx.coroutines.delay(500) // Update every 500ms
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LineChartView(context = context, dataPoints = dataPoints)
    }
}

@Composable
fun LineChartView(context: android.content.Context, dataPoints: List<Entry>) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            LineChart(ctx).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                description.text = "Dynamic Line Chart"
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
            }
        },
        update = { chart ->
            // Create and set LineDataSet
            val lineDataSet = LineDataSet(dataPoints, "Dynamic Data").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
            }

            val lineData = LineData(lineDataSet)
            chart.data = lineData
            chart.invalidate() // Refresh chart
        }
    )
}