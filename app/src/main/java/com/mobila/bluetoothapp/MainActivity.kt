package com.mobila.bluetoothapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobila.bluetoothapp.ui.sceens.HomeScreen
import com.mobila.bluetoothapp.ui.theme.BluetoothAppTheme
import com.mobila.bluetoothapp.ui.viewmodels.FakeVM

import com.mobila.bluetoothapp.ui.viewmodels.TrackerVMImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluetoothAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                  val trackerVm: TrackerVMImpl = viewModel(
//                      factory = TrackerVMImpl.Factory
//                    )
                    val fakeVM = FakeVM()
                    HomeScreen(fakeVM)

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothAppTheme {
        Greeting("Android")
    }
}