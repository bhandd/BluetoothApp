package com.mobila.bluetoothapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobila.bluetoothapp.model.MotionSensorHandler
import com.mobila.bluetoothapp.model.SensorData

interface MotionViewModelBase{
    val linearAcceleration: LiveData<SensorData>
    val sensorFusion: LiveData<SensorData>
    fun stopListening()
    fun toggleRecording()
}

class MotionVM(application: Application) : MotionViewModelBase, ViewModel() {
    private val motionSensorModel = MotionSensorHandler(application)

    override val linearAcceleration = motionSensorModel.linearAcceleration
    override val sensorFusion = motionSensorModel.sensorFusion

    override fun stopListening() {
        motionSensorModel.stopListening()
    }

    override fun toggleRecording() {
        motionSensorModel.toggleRecording()
    }

    class MotionVMFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MotionVM::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MotionVM(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class FakeVM(application: Application) : MotionViewModelBase {
    private val fakeMotionSensorModel = MotionSensorHandler(application)

    override val linearAcceleration = fakeMotionSensorModel.linearAcceleration
    override val sensorFusion = fakeMotionSensorModel.sensorFusion

    override fun stopListening() {
        fakeMotionSensorModel.stopListening()
    }

    override fun toggleRecording() {
        TODO("Not yet implemented")
    }
}