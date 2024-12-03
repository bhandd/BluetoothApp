package com.mobila.bluetoothapp.model

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.util.Log
import java.util.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Date

class MotionSensorHandler(application: Application) : SensorEventListener {

    private val sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometerSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val _accelerometerData = MutableLiveData<SensorData>()
    val accelerometerData: LiveData<SensorData> get() = _accelerometerData

    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val _gyroscopeData = MutableLiveData<SensorData>()
    val gyroscopeData: LiveData<SensorData> get() = _gyroscopeData

    var gravity = floatArrayOf(0f, 0f, 0f)


    //Save data
    private val accelerometerDataList = mutableListOf<SensorData>()
    private val gyroscopeDataList = mutableListOf<SensorData>()

    // Variabler för komplementärt filter
    var alpha = 0.98f // Filterfaktor, justera beroende på behov
    var fusedAngle = 0f // Resultatet av komplementärt filter
    var deltaT = 0.01f // Tidsintervall mellan mätningar (i sekunder)

    // Håll reda på vinkeln från gyroskop och accelerometer
    var gyroAngle = 0f
    var accelAngle = 0f

    var isRecording = false

    // Filhändelse (standard spara till appens lagringsmapp)
    //  private val fileDir = application.getExternalFilesDir(null)
    private val fileDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    init {

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
        //saveToCsv(accelerometerDataList, "AccelerometerData")
        saveAccGyroData()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val timestamp = formatTimestamp(System.currentTimeMillis())
                    val linear_acceleration = floatArrayOf(0f, 0f, 0f)

                    val alpha = 0.8f

                    // Isolate the force of gravity with the low-pass filter.
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                    //list to save data
                    val accelerometerData = SensorData(
                        x = gravity[0],
                        y = gravity[1],
                        z = gravity[2],
                        timeStamp = timestamp
                    )

                    accelerometerDataList.add(accelerometerData)
                    // Remove the gravity contribution with the high-pass filter. Som vi inte ska göra
//                    linear_acceleration[0]= event.values[0] - gravity.get(0)
//                    linear_acceleration[1] = event.values[1] - gravity.get(1)
//                    linear_acceleration[2] = event.values[2] - gravity.get(2)
                    // Create and post a MotionData object
                    _accelerometerData.postValue(
                        SensorData(
                            //isMotionDetected = isMotionDetected,
                            x = gravity[0],
                            y = gravity[1],
                            z = gravity[2],
                            timeStamp = formatTimestamp(System.currentTimeMillis())

//                            x = linear_acceleration[0],
//                            y = linear_acceleration[1],
//                            z = linear_acceleration[2]
                        )
                    )
                }

                Sensor.TYPE_GYROSCOPE -> {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val gyroData = SensorData(
                        x = x,
                        y = y,
                        z = z,
                        timeStamp = formatTimestamp(System.currentTimeMillis())
                    )

                    gyroscopeDataList.add(gyroData)
                    _gyroscopeData.postValue(
                        SensorData(
                            x = x,
                            y = y,
                            z = z,
                            timeStamp = formatTimestamp(System.currentTimeMillis())
                        )
                    )
                }
            }
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used for now
    }


    // Funktion för att uppdatera gyrovinkeln (integrering av gyroskopdata)
    fun updateGyroAngle(gyroRate: Float) {
        gyroAngle += gyroRate * deltaT // Integrera gyroskopets vinkelhastighet
    }

    // Funktion för att beräkna accelerometerns vinkel
    fun calculateAccelAngle(accelX: Float, accelY: Float, accelZ: Float): Float {
        // Exempel för att beräkna vinkeln i x-z-planet
        return Math.toDegrees(Math.atan2(accelY.toDouble(), accelZ.toDouble())).toFloat()
    }

    // Komplementärt filter
    fun applyComplementaryFilter(accelAngle: Float, gyroAngle: Float): Float {
        // Filtrera signalen med accelerometer och gyroskop
        return alpha * gyroAngle + (1 - alpha) * accelAngle
    }

    // Exempel: Uppdatera filtret med nya sensorvärden
    fun updateFilter(accelX: Float, accelY: Float, accelZ: Float, gyroRate: Float) {
        // Uppdatera gyrovinkeln
        updateGyroAngle(gyroRate)

        // Beräkna accelerometerns vinkel
        accelAngle = calculateAccelAngle(accelX, accelY, accelZ)

        // Använd komplementärt filter för att slå samman värdena
        fusedAngle = applyComplementaryFilter(accelAngle, gyroAngle)

        // Resultatet finns i fusedAngle
        println("Fused Angle: $fusedAngle")
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }


    /** function to save data to csv file
     *
     * **/
    private fun saveToCsv(dataList: List<SensorData>, fileName: String) {
        Log.d("MotionSensorHandler", "Saving data to file: ${fileDir?.absolutePath}")

        val file = fileDir?.resolve("$fileName.csv")
        file?.bufferedWriter().use { writer ->
            // Skriv rubriker till CSV-filen
            writer?.write("X;Y;Z;Time\n")

            // Skriv sensor data från listan
            dataList.forEach { data ->
                writer?.write("${data.x};${data.y};${data.z}; ${data.timeStamp}\n")
            }
        }
    }


    fun saveAccGyroData() {

        saveToCsv(accelerometerDataList, gyroscopeDataList, "Data")
    }

    private fun saveToCsv(
        dataList1: List<SensorData>,
        dataList2: List<SensorData>,
        fileName: String
    ) {
        Log.d("MotionSensorHandler", "Saving data to file: ${fileDir?.absolutePath}")

        val file = fileDir?.resolve("$fileName.csv")
        file?.bufferedWriter().use { writer ->
            // Skriv rubriker till CSV-filen
            writer?.write("aX;aY;aZ;Time\n")

            // Skriv sensor data från listan
            dataList1.forEach { data ->
                writer?.write("${data.x};${data.y};${data.z}; ${data.timeStamp};\n")
                // Lägg till en tom rad som separator
            }
                writer?.write("\n")

                // Skriv rubriker för andra datauppsättningen
                writer?.write("gX;gY;gZ;Time\n")

                // Skriv data från dataList2
                dataList2.forEach { data ->
                    writer?.write("${data.x};${data.y};${data.z};${data.timeStamp}\n")
                }

        }

    }
        fun toggleRecording() {
            if (isRecording) {
                stopListening()
            } else {
                startListening()
            }
            isRecording = !isRecording
        }

}

data class SensorData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timeStamp: String
)

data class AccelerometerData (
    val velX: Float,
    val velY: Float,
    val velZ: Float,
    val timestamp: Long
)

data class GyroscopeData(
    val angularX: Float,
    val angularY: Float,
    val angularZ: Float,
    val timestamp: Long
)


