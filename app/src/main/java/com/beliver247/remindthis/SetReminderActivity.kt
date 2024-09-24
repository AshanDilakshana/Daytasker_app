package com.beliver247.remindthis

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Calendar

class SetReminderActivity : AppCompatActivity() {

    private lateinit var selectedTimeText: TextView
    private lateinit var reminderTitle: EditText
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_reminder)

        selectedTimeText = findViewById(R.id.selectedTime)
        reminderTitle = findViewById(R.id.ReminderTitle)
        val selectTimeBtn: Button = findViewById(R.id.selectTimeBtn)
        val setAlarmBtn: Button = findViewById(R.id.setAlarmBtn)

        // Initialize the calendar
        calendar = Calendar.getInstance()

        // Time picker dialog
        selectTimeBtn.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Set the time on the calendar
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    // Display selected time
                    selectedTimeText.text = String.format("%02d:%02d", hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        // Set alarm button click
        setAlarmBtn.setOnClickListener {
            val title = reminderTitle.text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a reminder title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (calendar.timeInMillis > System.currentTimeMillis()) {
                setAlarm(calendar.timeInMillis, title)
                Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Selected time is in the past", Toast.LENGTH_SHORT).show()
            }
        }

        // Request notification permission if required (Android 13+)
        requestNotificationPermission()
    }

    private fun setAlarm(timeInMillis: Long, title: String) {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("REMINDER_TITLE", title) // Passing the title to AlarmReceiver
        }

        pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted
                }
                else -> {
                    // Request the permission
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
