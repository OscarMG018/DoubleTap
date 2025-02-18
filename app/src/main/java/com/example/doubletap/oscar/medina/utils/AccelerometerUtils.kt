import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class AccelerometerUtils : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var linearAccelerationSensor: Sensor? = null
    private var onTapListeners: MutableList<() -> Unit> = mutableListOf()
    private var onDoubleTapListeners: MutableList<() -> Unit> = mutableListOf()
    private var onSensorChangedListeners: MutableList<(FloatArray) -> Unit> = mutableListOf()
    
    private val TAP_THRESHOLD = 10f
    private val MAX_TAP_LENGTH = 500L
    private var DOUBLE_TAP_MAX_INTERVAL = 500L

    private var insideTap = false
    private var lastTapTime = 0L
    private var lastDoubleTapTime = 0L


    fun init(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        linearAccelerationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        addOnTapListener { dubleTapHandler() }
    }
    
    fun addOnTapListener(listener: () -> Unit) {
        onTapListeners.add(listener)
    }

    fun removeOnTapListener(listener: () -> Unit) {
        onTapListeners.remove(listener)
    }

    fun addOnDoubleTapListener(listener: () -> Unit) {
        onDoubleTapListeners.add(listener)
    }

    fun removeOnDoubleTapListener(listener: () -> Unit) {
        onDoubleTapListeners.remove(listener)
    }

    fun addOnSensorChangedListener(listener: (FloatArray) -> Unit) {
        onSensorChangedListeners.add(listener);
    }
    
    fun removeOnSensorChangedListener(listener: (FloatArray) -> Unit) {
        onSensorChangedListeners.remove(listener)
    }

    fun _triggerOnTap() {
        onTapListeners.forEach { it.invoke() }
    }

    fun _triggerOnDoubleTap() {
        onDoubleTapListeners.forEach { it.invoke() }
    }

    fun startListening() {
        sensorManager?.registerListener(
            this,
            linearAccelerationSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    
    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Tap trigger
            if (it.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                onSensorChangedListeners.forEach { listener ->
                    listener.invoke(it.values)
                }
                val magnitude = sqrt(
                    it.values[0] * it.values[0] +
                    it.values[1] * it.values[1] +
                    it.values[2] * it.values[2]
                )
                
                if (magnitude > TAP_THRESHOLD) {
                    if (!insideTap) {
                        insideTap = true
                        lastTapTime = System.currentTimeMillis()
                    } 
                } else {
                    if (insideTap) {
                        insideTap = false
                        var tapLengt = System.currentTimeMillis() - lastTapTime
                        if (tapLengt > MAX_TAP_LENGTH) {
                            tapLengt = 0
                        }
                        if (tapLengt < MAX_TAP_LENGTH) {
                            _triggerOnTap()
                        }
                    }
                }
            }
        }
    }

    fun dubleTapHandler() {
        val timeDifference = System.currentTimeMillis() - lastDoubleTapTime
        if (lastDoubleTapTime != 0L && timeDifference < DOUBLE_TAP_MAX_INTERVAL) {
            _triggerOnDoubleTap()
            lastDoubleTapTime = 0
        } else {
            lastDoubleTapTime = System.currentTimeMillis()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}