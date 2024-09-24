package com.beliver247.remindthis

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddReminderActivity : AppCompatActivity() {

    private var selectedTimeInMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        val titleEditText = findViewById<EditText>(R.id.editTextReminderTitle)
        val timeTextView = findViewById<TextView>(R.id.textViewReminderTime)
        val pickTimeButton = findViewById<Button>(R.id.btn_pick_time)
        val saveButton = findViewById<Button>(R.id.btn_save)

        // Handle reminder ID to check if it's a new reminder or an edit
        val reminderId = intent.getLongExtra("reminderId", -1)
        if (reminderId != -1L) {
            // Populate fields if editing an existing reminder
            titleEditText.setText(intent.getStringExtra("reminderTitle"))
            selectedTimeInMillis = intent.getLongExtra("reminderTime", 0L)
            if (selectedTimeInMillis > 0L) {
                timeTextView.text = formatTime(selectedTimeInMillis)
            }
        }

        // Time picker for selecting reminder time
        pickTimeButton.setOnClickListener {
            showTimePicker(timeTextView)
        }

        // Save the reminder
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()

            // Validate input before saving
            if (title.isEmpty() || selectedTimeInMillis == 0L) {
                Toast.makeText(this, "Please set a title and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create intent to return the data to MainActivity
            val resultIntent = Intent().apply {
                putExtra("reminderId", reminderId)
                putExtra("reminderTitle", title)
                putExtra("reminderTime", selectedTimeInMillis)
            }
            setResult(RESULT_OK, resultIntent) // Return success result
            finish() // Close the activity
        }
    }

    // Show TimePickerDialog to set the time
    private fun showTimePicker(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Initialize TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }
            selectedTimeInMillis = calendar.timeInMillis // Set selected time
            timeTextView.text = formatTime(selectedTimeInMillis) // Update TextView
        }, currentHour, currentMinute, true)

        timePickerDialog.show()
    }

    // Format the selected time into a readable format (HH:mm)
    private fun formatTime(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return String.format("%02d:%02d", hour, minute) // Return formatted time
    }
}
