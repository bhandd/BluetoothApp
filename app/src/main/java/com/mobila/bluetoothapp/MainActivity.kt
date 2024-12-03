package com.mobila.bluetoothapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobila.bluetoothapp.model.NavigationController
import com.mobila.bluetoothapp.ui.sceens.GraphScreen
import com.mobila.bluetoothapp.ui.sceens.HomeScreen
import com.mobila.bluetoothapp.ui.theme.BluetoothAppTheme
import com.mobila.bluetoothapp.ui.viewmodels.MotionVM

class MainActivity : ComponentActivity() {

    private lateinit var motionViewModel: MotionVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = MotionVM.MotionVMFactory(application)
        motionViewModel = ViewModelProvider(this, factory).get(MotionVM::class.java)

        enableEdgeToEdge()
        setContent {
            BluetoothAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavigationController.setNavController(navController)

                    NavHost(navController = navController, startDestination = "HomeScreen") {
                        composable("HomeScreen") {
                            HomeScreen(vm = motionViewModel)
                        }
                        composable("GraphScreen") {
                            GraphScreen(vm = motionViewModel)
                        }
                    }
                }
            }
        }
    }
}
