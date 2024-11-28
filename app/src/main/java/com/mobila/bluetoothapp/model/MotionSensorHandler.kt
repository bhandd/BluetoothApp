package com.mobila.bluetoothapp.model

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MotionSensorHandler(application: Application) : SensorEventListener {

    private val sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val motionSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _motionData = MutableLiveData<MotionData>()
    val motionData: LiveData<MotionData> get() = _motionData

    init {
        startListening()
    }

    private fun startListening() {
        motionSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val timestamp = System.currentTimeMillis()

            // Simple motion detection logic
            val isMotionDetected = (x > 1 || y > 1 || z > 1)

            // Create and post a MotionData object
            _motionData.postValue(
                MotionData(
                    isMotionDetected = isMotionDetected,
                    x = x,
                    y = y,
                    z = z,
                    timestamp = timestamp
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used for now
    }
}

data class MotionData(
    val isMotionDetected: Boolean,
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)