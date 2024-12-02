package com.mobila.bluetoothapp.ui.sceens

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mobila.bluetoothapp.model.NavigationController
import com.mobila.bluetoothapp.model.SensorData
import com.mobila.bluetoothapp.ui.viewmodels.MotionViewModelBase

@Composable
fun GraphScreen (
    vm: MotionViewModelBase
) {
    val accelerometerLiveData: LiveData<SensorData> = vm.accelerometerData
    val gyroscopeLiveData: LiveData<SensorData> = vm.gyroscopeData

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8855BB),
                        Color(0xFF22AA88)
                    )
                )
            )
    ) {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(8.dp)
            ) {
                Button(
                    onClick = {
                        NavigationController.navigate("HomeScreen")
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Data",
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = "Measurements",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    Text(
                        text = "Accelerometer Data",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    DataGraph(
                        liveData = accelerometerLiveData,
                        yRange = 40f
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    Text(
                        text = "Gyroscope Data",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    DataGraph(
                        liveData = gyroscopeLiveData,
                        yRange = 20f
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Simon, Björn - KTH",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun AccelerometerGraph (
    vm: MotionViewModelBase
) {
    val sensorData = vm.accelerometerData.observeAsState()
    val dataPoints = remember { mutableStateListOf<SensorData>() }

    sensorData.value?.let {
        if (dataPoints.size > 200) {
            dataPoints.removeAt(0)
        }
        dataPoints.add(it)
    }
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                //setDrawGridBackground(true)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                legend.isEnabled = true
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                }
                axisLeft.apply {
                    axisMaximum = 15f // Set max value for Y-axis
                    axisMinimum = -15f // Set min value for Y-axis
                    setDrawGridLines(true) // Draw grid lines
                }
                axisRight.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        update = { chart ->
            val lineData = createLineData(dataPoints)
            chart.data = lineData
            chart.invalidate()
        }
    )
}



@Composable
fun GyroscopeGraph (
    vm: MotionViewModelBase
) {
    val gyroscopeData = vm.gyroscopeData.observeAsState()
    val dataPoints = remember { mutableStateListOf<SensorData>() }

    gyroscopeData.value?.let {
        if (dataPoints.size > 200) {
            dataPoints.removeAt(0)
        }
        dataPoints.add(it)
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                //setDrawGridBackground(true)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                legend.isEnabled = true
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                }
                axisLeft.apply {
                    axisMaximum = 15f // Set max value for Y-axis
                    axisMinimum = -15f // Set min value for Y-axis
                    setDrawGridLines(true) // Draw grid lines
                }
                axisRight.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        update = { chart ->
            val lineData = createLineData(dataPoints)
            chart.data = lineData
            chart.invalidate()
        }
    )
}

@Composable
fun DataGraph(
    liveData: LiveData<SensorData>, yRange: Float
) {
    val data = liveData.observeAsState()
    val dataPoints = remember { mutableStateListOf<SensorData>() }

    data.value?.let {
        if (dataPoints.size > 200) {
            dataPoints.removeAt(0)
        }
        dataPoints.add(it)
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                //setDrawGridBackground(true)
                setTouchEnabled(false)
                isDragEnabled = false
                setScaleEnabled(true)
                setPinchZoom(false)
                legend.isEnabled = true
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                }
                axisLeft.apply {
                    axisMaximum = yRange // Set max value for Y-axis
                    axisMinimum = -yRange // Set min value for Y-axis
                    setDrawGridLines(true) // Draw grid lines
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val lineData = createLineData(dataPoints)
            chart.data = lineData
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

fun createLineData(data: List<SensorData>): LineData {
    val xEntries = data.mapIndexed { index, point -> Entry(index.toFloat(), point.x) }
    val yEntries = data.mapIndexed { index, point -> Entry(index.toFloat(), point.y) }
    val zEntries = data.mapIndexed { index, point -> Entry(index.toFloat(), point.z) }

    val xDataSet = LineDataSet(xEntries, "X-Axis").apply {
        color = android.graphics.Color.RED
        valueTextColor = android.graphics.Color.RED
        lineWidth = 2f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawCircles(false)
        setDrawValues(false)
    }
    val yDataSet = LineDataSet(yEntries, "Y-Axis").apply {
        color = android.graphics.Color.BLUE
        valueTextColor = android.graphics.Color.BLUE
        lineWidth = 2f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawCircles(false)
        setDrawValues(false)
    }
    val zDataSet = LineDataSet(zEntries, "Z-Axis").apply {
        color = android.graphics.Color.GREEN
        valueTextColor = android.graphics.Color.GREEN
        lineWidth = 2f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawCircles(false)
        setDrawValues(false)
    }
    return LineData(xDataSet, yDataSet, zDataSet)
}