package com.beliver247.remindthis

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity

class StopwatchActivity : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private var running = false
    private var pauseOffset: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        chronometer = findViewById(R.id.chronometer)
        val startButton = findViewById<Button>(R.id.btn_start)
        val stopButton = findViewById<Button>(R.id.btn_stop)
        val resetButton = findViewById<Button>(R.id.btn_reset)

        startButton.setOnClickListener {
            if (!running) {
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                running = true

                // Start service to keep stopwatch running in background
                val serviceIntent = Intent(this, StopwatchService::class.java)
                startService(serviceIntent)
            }
        }

        stopButton.setOnClickListener {
            if (running) {
                chronometer.stop()
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
                running = false

                // Stop the service when the stopwatch is paused
                stopService(Intent(this, StopwatchService::class.java))
            }
        }

        resetButton.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            running = false
            chronometer.stop()

            // Stop the service when the stopwatch is reset
            stopService(Intent(this, StopwatchService::class.java))
        }

    }

    override fun onPause() {
        super.onPause()

        // Save the current chronometer state in SharedPreferences
        val sharedPref = getSharedPreferences("StopwatchPref", MODE_PRIVATE).edit()
        sharedPref.putLong("base", chronometer.base)
        sharedPref.putLong("pauseOffset", pauseOffset)
        sharedPref.putBoolean("running", running)
        sharedPref.apply()

        // Stop the service only if the stopwatch is not running
        if (!running) {
            stopService(Intent(this, StopwatchService::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        // Restore the chronometer state from SharedPreferences
        val sharedPref = getSharedPreferences("StopwatchPref", MODE_PRIVATE)
        chronometer.base = sharedPref.getLong("base", SystemClock.elapsedRealtime())
        pauseOffset = sharedPref.getLong("pauseOffset", 0)
        running = sharedPref.getBoolean("running", false)

        if (running) {
            // If the stopwatch was running, restart the chronometer
            chronometer.start()
            // Ensure service is running if the stopwatch is running
            startService(Intent(this, StopwatchService::class.java))
        } else {
            // If it was paused, adjust the base without starting the chronometer
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
        }
    }

}
