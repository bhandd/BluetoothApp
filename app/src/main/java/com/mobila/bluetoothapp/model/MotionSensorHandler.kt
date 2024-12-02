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

    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val _accelerometerData = MutableLiveData<AccelerometerData>()
    val accelerometerData: LiveData<AccelerometerData> get() = _accelerometerData

    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val _gyroscopeData = MutableLiveData<GyroscopeData>()
    val gyroscopeData: LiveData<GyroscopeData> get() = _gyroscopeData

    init {
        startListening()
    }

    private fun startListening() {
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val timestamp = System.currentTimeMillis()

                    // Simple motion detection logic
                    val isMotionDetected = (x > 1 || y > 1 || z > 1)

                    // Create and post a MotionData object
                    _accelerometerData.postValue(
                        AccelerometerData(
                            //isMotionDetected = isMotionDetected,
                            x = x,
                            y = y,
                            z = z,
                            timestamp = timestamp
                        )
                    )
                }
                Sensor.TYPE_GYROSCOPE -> {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    _gyroscopeData.postValue(
                        GyroscopeData(
                            angularX = x,
                            angularY = y,
                            angularZ = z,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used for now
    }
}

data class AccelerometerData(
    //val isMotionDetected: Boolean,
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)

data class GyroscopeData(
    val angularX: Float,
    val angularY: Float,
    val angularZ: Float,
    val timestamp: Long
)