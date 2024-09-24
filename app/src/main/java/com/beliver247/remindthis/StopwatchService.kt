package com.beliver247.remindthis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import androidx.core.app.NotificationCompat

class StopwatchService : Service() {

    private lateinit var chronometer: Chronometer
    private val channelId = "StopwatchChannel"

    override fun onCreate() {
        super.onCreate()

        chronometer = Chronometer(this)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, StopwatchActivity::class.java)

        // Updated PendingIntent with FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Stopwatch is running")
            .setContentText("Tap to return to the stopwatch")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Stopwatch Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check if the stopwatch was running or paused and handle the state
        val sharedPref = getSharedPreferences("StopwatchPref", MODE_PRIVATE)
        val isRunning = sharedPref.getBoolean("running", false)

        if (!isRunning) {
            // Stop service if stopwatch is not running
            stopSelf()
        }

        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
