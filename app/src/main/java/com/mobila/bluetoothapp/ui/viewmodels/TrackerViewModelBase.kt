package com.mobila.bluetoothapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mobila.bluetoothapp.TrackerApplication
import com.mobila.bluetoothapp.model.Tracker

interface TrackerViewModelBase {
    val x: Int
    val y: Int
    val z: Int

    fun getCoordinates(): String {
        return "x: $x, y: $y, z: $z"
    }
}

class TrackerVMImpl : TrackerViewModelBase, ViewModel() {

    override val x: Int = 0
    override val y: Int = 0
    override val z: Int = 0

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                // Hämta applikationen från context
//                val application = (this[APPLICATION_KEY] as TrackerApplication)
//                // Hämta instans av WeatherLogic från applikationen
//                val trackerModel = application.trackerModel
//                // Skapa WeatherVM
//                WeatherVM(weatherLogic)
//            }
//        }
//    }
}

class FakeVM(): TrackerViewModelBase {
    override val x: Int = 0
    override val y: Int = 0
    override val z: Int = 0
}