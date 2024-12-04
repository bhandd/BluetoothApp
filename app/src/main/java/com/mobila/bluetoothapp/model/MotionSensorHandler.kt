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

import android.graphics.Bitmap
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import java.io.File
import java.io.FileOutputStream
import kotlin.math.atan2
import kotlin.math.sqrt

class MotionSensorHandler(application: Application) : SensorEventListener {

    private val sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)


    // Raw data
    private val _accelerometerData = MutableLiveData<SensorData>()
    val accelerometerData: LiveData<SensorData> get() = _accelerometerData
    // Raw data
    private val _gyroscopeData = MutableLiveData<SensorData>()
    val gyroscopeData: LiveData<SensorData> get() = _gyroscopeData

    //Save data
    private val _linearAccelerationDataList = mutableListOf<SensorData>()
    private val _linearAccelerationLiveData = MutableLiveData<SensorData>()
    private var _elevationAngle = MutableLiveData<Float>()
    val linearAcceleration: LiveData<SensorData> get() = _linearAccelerationLiveData
   // val elevationAngle: LiveData<SensorData> get() = _elevationAngle

    private val _sensorFusionDataList = mutableListOf<SensorData>()
    private val _sensorFusionLiveData = MutableLiveData<SensorData>()
    val sensorFusion: LiveData<SensorData> get() = _sensorFusionLiveData

    var previousMeasuredAccData = floatArrayOf(0f, 0f, 0f)

    // Variabler för komplementärt filter
    var alpha = 0.8f // Filterfaktor, justera beroende på behov


    // Håll reda på vinkeln från gyroskop och accelerometer
    var gyroAngle = 0f
    var accelAngle = 0f

    var isRecording = false

    // Filhändelse (standard spara till appens lagringsmapp)
    //  private val fileDir = application.getExternalFilesDir(null)
    private val fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    init {
        _accelerometerData.observeForever { accelerometerData ->
            accelerometerData?.let {
                // Skapa en modifierad version av accelerometer-data
                val modifiedData = modifyAccelerometerData(it)
                //val elevationAngle = calculateElevationAngle(modifiedData.x, modifiedData.y, modifiedData.z)

                // Lägg till den modifierade datan i linearAccelerationDataList
                _linearAccelerationLiveData.postValue(modifiedData)
                _linearAccelerationDataList.add(modifiedData)
               // _elevationAngle.postValue(elevationAngle)
            }
        }
        _accelerometerData.observeForever { accelerometerData ->
            accelerometerData?.let {
                updateSensorFusionData(it, _gyroscopeData.value)
            }
        }
        _gyroscopeData.observeForever { gyroscopeData ->
            gyroscopeData?.let {
                updateSensorFusionData(_accelerometerData.value, it)
            }
        }
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
        saveAccGyroData()
        /*if (saveChartToFile(lineChart, "my_chart")) {
            Toast.makeText(this, "Graf sparad!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Misslyckades att spara grafen", Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val timestamp = formatTimestamp(System.currentTimeMillis())


                    _accelerometerData.postValue(
                        SensorData(
                            x = x,
                            y = y,
                            z = z,
                            elevationAngle = calculateElevationAngle(x, y, z), //todo: move this
                            timeStamp = timestamp

                        )
                    )
                }

                Sensor.TYPE_GYROSCOPE -> {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val timestamp = formatTimestamp(System.currentTimeMillis())




                    _gyroscopeData.postValue(
                        SensorData(
                            x = x,
                            y = y,
                            z = z,
                            elevationAngle = calculateElevationAngle(x, y, z), //todo: move this
                            timeStamp = timestamp
                        )
                    )
                }
            }
        }
    }

    private fun modifyAccelerometerData(data: SensorData): SensorData {
        val modifiedX = alpha * data.x + (1 - alpha) * previousMeasuredAccData[0]
        val modifiedY = alpha * data.y + (1 - alpha) * previousMeasuredAccData[1]
        val modifiedZ = alpha * data.z + (1 - alpha) * previousMeasuredAccData[2]

        //Log.d("test1", ": " + modifiedX + ", " + modifiedY + ", " + modifiedZ)
        previousMeasuredAccData[0] = modifiedX
        previousMeasuredAccData[1] = modifiedY
        previousMeasuredAccData[2] = modifiedZ
        return SensorData(
            x = modifiedX,
            y = modifiedY,
            z = modifiedZ,
            elevationAngle = calculateElevationAngle(modifiedX, modifiedY, modifiedZ),
            timeStamp = data.timeStamp
        )
    }

    private fun updateSensorFusionData(accelerometerData: SensorData?, gyroscopeData: SensorData?) {
        if (accelerometerData != null && gyroscopeData != null) {
            //Log.d("test2", ": " + gyroscopeData.x + ", " + gyroscopeData.y + ", " + gyroscopeData.z)
            val fusedData = SensorData(
                x = alpha * accelerometerData.x + (1 - alpha) * gyroscopeData.x,
                y = alpha * accelerometerData.y + (1 - alpha) * gyroscopeData.y,
                z = alpha * accelerometerData.z + (1 - alpha) * gyroscopeData.z,
                elevationAngle = calculateElevationAngle(accelerometerData.x, accelerometerData.y, accelerometerData.z),
                timeStamp = accelerometerData.timeStamp,
            )
            _sensorFusionLiveData.postValue(fusedData)
            _sensorFusionDataList.add(fusedData)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used for now
    }






    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /** function to save data to csv file
     *
     * **/
    private fun saveElevationAngleToCsv(dataList: List<SensorData>, fileName: String) {
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
        saveToCsv(_linearAccelerationDataList, _sensorFusionDataList, "Data")
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
            writer?.write("fX;fY;fZ;Time\n")
            // Skriv data från dataList2
            dataList2.forEach { data ->
                writer?.write("${data.x};${data.y};${data.z}; ${data.timeStamp}\n")
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

    fun saveChartToFile(lineChart: LineChart, fileName: String): Boolean {
        try {
            // Skapa en bitmap från grafen
            val bitmap: Bitmap = lineChart.chartBitmap

            // Hitta katalogen där vi vill spara bilden
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Charts")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Spara filen
            val file = File(directory, "$fileName.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return true // Lyckad sparning
        } catch (e: Exception) {
            e.printStackTrace()
            return false // Fel vid sparning
        }
    }


    fun calculateElevationAngle(x: Float, y: Float, z: Float): Float {
        // Beräkna elevation angle i grader
        val elevationAngleRadians = atan2(z.toDouble(), sqrt((x * x + y * y).toDouble()))
        val elevationAngleDegrees = Math.toDegrees(elevationAngleRadians)
        return elevationAngleDegrees.toFloat()
    }
}

data class SensorData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timeStamp: String,
    val elevationAngle: Float
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


