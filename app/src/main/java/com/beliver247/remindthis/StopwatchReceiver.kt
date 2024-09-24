package com.beliver247.remindthis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopwatchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPref = context.getSharedPreferences("StopwatchPref", Context.MODE_PRIVATE)
            val wasRunning = sharedPref.getBoolean("running", false)

            if (wasRunning) {
                // Restart the stopwatch service only if it was running before boot
                val serviceIntent = Intent(context, StopwatchService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }

}

