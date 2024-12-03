package com.mobila.bluetoothapp.ui.sceens

import android.content.Context
import android.graphics.Color
import android.widget.AbsListView.LayoutParams
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mobila.bluetoothapp.ui.viewmodels.FakeVM
import com.mobila.bluetoothapp.ui.viewmodels.MotionViewModelBase
import kotlinx.coroutines.delay

@Composable
fun GraphScreen (
    vm: MotionViewModelBase
) {
    val context = LocalContext.current
    val dataPoints = remember { mutableStateListOf<Entry>() }
    var motionData = vm.accelerometerData.observeAsState()

    // Simulate dynamic data updates
    LaunchedEffect(Unit) {
        var x = 0f
        while (x < 100) {
           // dataPoints.add(Entry(x, motionData.value?.x ?: 1f))
           // dataPoints.add(Entry(x, 1f))
            dataPoints.add(Entry(x, (Math.random()*100).toFloat()))
            x += 1f
            delay(500) // Update every 500ms
        }
    }
Column(
    modifier = Modifier.padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally)
    {     Box(modifier = Modifier.fillMaxSize()) {
    LineChartView(context = context, dataPoints = dataPoints)

}
Spacer(modifier = Modifier.padding(16.dp))
}



}

@Composable
fun LineChartView(context: Context, dataPoints: List<Entry>) {
    val maxVisiblePoints = 100 // Maximalt antal punkter som syns på skärmen
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            LineChart(ctx).apply {
                layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                description.text = "Dynamic Line Chart"
                setTouchEnabled(false)
                isDragEnabled = false
                setScaleEnabled(false)
                //setPinchZoom(true)
            }
        },
        update = { chart ->
            // Create and set LineDataSet
            val lineDataSet = LineDataSet(dataPoints, "Dynamic Data").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val lineData = LineData(lineDataSet)
            chart.data = lineData
            chart.invalidate() // Refresh chart


//            chart.setVisibleXRangeMaximum(maxVisiblePoints.toFloat())
            chart.moveViewToX(dataPoints.size.toFloat())
            chart.invalidate()
        }
    )
}


