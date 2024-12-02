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

import com.mobila.bluetoothapp.model.AccelerometerData
import com.mobila.bluetoothapp.model.GyroscopeData
import com.mobila.bluetoothapp.ui.viewmodels.FakeVM
import com.mobila.bluetoothapp.ui.viewmodels.MotionViewModelBase

import androidx.compose.foundation.Canvas

import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay


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
                Text(
                    text = "Header",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineMedium
                )
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
                    text = "Footer",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun MiddleElement(vm: MotionViewModelBase) {
    val motionData = vm.accelerometerData.observeAsState()
    val gyroscopeData = vm.gyroscopeData.observeAsState()
    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (motionData.value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val data = motionData.value!!
                    AccelerometerDataDisplay(data)
                }
            } else {
                Text(text = "Waiting for motion data...", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (gyroscopeData.value != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val data = gyroscopeData.value!!
                    GyroscopeDataDisplay(data)


                }
            } else {
                Text(text = "Waiting for gyroscope data...", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AccelerometerDataDisplay(accelerometerData: AccelerometerData) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Accelerometer Detected:",
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "X: ${accelerometerData.x}",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            text = "Y: ${accelerometerData.y}",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            text = "Z: ${accelerometerData.z}",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Timestamp: ${accelerometerData.timestamp}",
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}

@Composable
fun GyroscopeDataDisplay(gyroscopeData: GyroscopeData) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gyroscope Data:",
            textAlign = TextAlign.Center,
            fontSize = 20.sp)
        Text(
            text = "X: ${gyroscopeData.angularX}",
            textAlign = TextAlign.Center,
            fontSize = 18.sp)
        Text(
            text = "Y: ${gyroscopeData.angularY}",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            text = "Z: ${gyroscopeData.angularZ}",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            text = "Timestamp: ${gyroscopeData.timestamp}",
            textAlign = TextAlign.Center,
            fontSize = 16.sp
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

