package com.mobila.bluetoothapp.ui.sceens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mobila.bluetoothapp.ui.viewmodels.FakeVM
import com.mobila.bluetoothapp.ui.viewmodels.MotionViewModelBase

import com.mobila.bluetoothapp.model.NavigationController
import com.mobila.bluetoothapp.model.SensorData
import com.mobila.bluetoothapp.model.utils.GraphUtils
import java.io.File

@Composable
fun HomeScreen(
    vm: MotionViewModelBase
) {
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
                        NavigationController.navigate("GraphScreen")
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Graphs",
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
                Button(
                    onClick = {
                        vm.toggleRecording()
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Start/stop",
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                MiddleElement(vm)
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
fun MiddleElement(vm: MotionViewModelBase) {
    val linearData = vm.storedData.observeAsState()
    val sensorFusion = vm.sensorFusion.observeAsState()

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (linearData.value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val data = linearData.value!!
                    AccelerometerDataDisplay(linearData.value!!)
                }
            } else {
                Text(text = "Waiting for motion data...", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sensorFusion.value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val data = sensorFusion.value!!
                    GyroscopeDataDisplay(data)
                }
            } else {
                Text(text = "Waiting for gyroscope data...", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AccelerometerDataDisplay(accelerometerData: SensorData) {
    val graphUtils = GraphUtils()
    val lineData = graphUtils.createLineData(listOf(accelerometerData))
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Linear acceleration data:",
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
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
                        axisMaximum = 40f
                        axisMinimum = -40f
                        setDrawGridLines(true)
                    }
                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                chart.data = lineData
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )


        Spacer(modifier = Modifier.height(8.dp))
    }


}

fun invalidate() {
    TODO("Not yet implemented")
}

@Composable
fun GyroscopeDataDisplay(gyroscopeData: SensorData) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sensor fusion data:",
            textAlign = TextAlign.Center,
            fontSize = 20.sp)
        Text(
            text = "X: ${ (Math.round(gyroscopeData.x*100))/100f }",
            textAlign = TextAlign.Center,
            fontSize = 18.sp)
        Text(
            text = "Y: ${ (Math.round(gyroscopeData.y*100))/100f }",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            text = "Z: ${ (Math.round(gyroscopeData.z*100))/100f }",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Surface(){
        HomeScreen(FakeVM(application = Application()))
    }
}


