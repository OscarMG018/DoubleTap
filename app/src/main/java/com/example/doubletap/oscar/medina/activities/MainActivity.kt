package com.example.doubletap.oscar.medina.activities

import AccelerometerUtils
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doubletap.oscar.medina.R
class MainActivity : AppCompatActivity() {

    private val accelerometerUtils = AccelerometerUtils()
    private lateinit var xAcc: TextView
    private lateinit var yAcc: TextView
    private lateinit var zAcc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        xAcc = findViewById(R.id.xAcc)
        yAcc = findViewById(R.id.yAcc)
        zAcc = findViewById(R.id.zAcc)

        accelerometerUtils.init(this)
        accelerometerUtils.startListening()
        accelerometerUtils.addOnSensorChangedListener { values ->
            onSensorChanged(values)
        }
    }

    fun onSensorChanged(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]
        xAcc.text = x.toString()
        yAcc.text = y.toString()
        zAcc.text = z.toString()
    }
}
