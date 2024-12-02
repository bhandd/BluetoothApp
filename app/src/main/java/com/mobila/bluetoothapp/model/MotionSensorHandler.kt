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

    //Filtering gravity
    val linear_acceleration = FloatArray(3)
    val gravity = floatArrayOf(0f, 0f, 0f) //Todo: Move this to a class field
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
//                    var x = event.values[0]
//                    var y = event.values[1]
//                    var z = event.values[2]
                    val timestamp = System.currentTimeMillis()

                    // Simple motion detection logic
                   // val isMotionDetected = (x > 1 || y > 1 || z > 1)

                    // Isolate the force of gravity with the low-pass filter.


                    val alpha = 0.8f

                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                    // Remove the gravity contribution with the high-pass filter.
                    linear_acceleration[0]= event.values[0] - gravity.get(0)
                    linear_acceleration[1] = event.values[1] - gravity.get(1)
                    linear_acceleration[2] = event.values[2] - gravity.get(2)

                    // Create and post a MotionData object
                    _accelerometerData.postValue(
                        AccelerometerData(
//                            //isMotionDetected = isMotionDetected,
//                            x = x,
//                            y = y,
//                            z = z,
                            x = linear_acceleration[0],
                            y =  linear_acceleration[1],
                            z = linear_acceleration[2],
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