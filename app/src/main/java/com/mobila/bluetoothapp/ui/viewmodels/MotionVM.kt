package com.mobila.bluetoothapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobila.bluetoothapp.model.MotionData
import com.mobila.bluetoothapp.model.MotionSensorHandler

interface MotionViewModelBase{
    val motionData: LiveData<MotionData>
}

class MotionVM(application: Application) : MotionViewModelBase, ViewModel() {
    private val motionSensorModel = MotionSensorHandler(application)

    override val motionData = motionSensorModel.motionData

    fun stopListening() {
        motionSensorModel.stopListening()
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

    override val motionData = fakeMotionSensorModel.motionData

    fun stopListening() {
        fakeMotionSensorModel.stopListening()
    }
}