package com.beliver247.remindthis

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val addReminderButton = findViewById<Button>(R.id.btn_add_reminder)
        val openStopwatchButton = findViewById<Button>(R.id.btn_open_stopwatch)
        val setReminder = findViewById<Button>(R.id.btn_openNotification)

        recyclerView.layoutManager = LinearLayoutManager(this)
        reminderAdapter = ReminderAdapter(reminders, this::editReminder, this::deleteReminder)
        recyclerView.adapter = reminderAdapter

        loadReminders()

        addReminderButton.setOnClickListener {
            val intent = Intent(this, AddReminderActivity::class.java)
            startActivityForResult(intent, ADD_REMINDER_REQUEST)
        }

        openStopwatchButton.setOnClickListener {
            val intent = Intent(this, StopwatchActivity::class.java)
            startActivity(intent)
        }

        setReminder.setOnClickListener{
            val intent = Intent(this, SetReminderActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_REMINDER_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                val reminderId = it.getLongExtra("reminderId", -1)
                val title = it.getStringExtra("reminderTitle") ?: ""
                val time = it.getLongExtra("reminderTime", 0L)

                if (time == 0L) {
                    // Handle invalid time case
                    return@let
                }

                if (reminderId == -1L) {
                    // New reminder
                    val reminder = Reminder(System.currentTimeMillis(), title, time)
                    reminders.add(reminder)
                } else {
                    // Edit existing reminder
                    reminders.find { r -> r.id == reminderId }?.apply {
                        this.title = title
                        this.time = time
                    }
                }

                // Sort reminders after adding or editing based on the time field
                reminders.sortBy { it.time }

                // Save and update the UI
                saveReminders()
                reminderAdapter.notifyDataSetChanged()

            }
        }
    }


    private fun saveReminders() {
        val sharedPreferences = getSharedPreferences("reminders", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        editor.putString("reminders", gson.toJson(reminders))
        editor.apply()
    }

    private fun loadReminders() {
        val sharedPreferences = getSharedPreferences("reminders", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reminders", "[]") // Default to an empty array
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Reminder>>() {}.type
            val savedReminders: MutableList<Reminder> = gson.fromJson(json, type)
            reminders.clear()
            reminders.addAll(savedReminders)

            // Sort reminders by time (closest to farthest)
            reminders.sortBy { it.time }
        }
        reminderAdapter.notifyDataSetChanged() // Refresh the RecyclerView with the loaded data
    }

    private fun editReminder(reminder: Reminder) {
        val intent = Intent(this, AddReminderActivity::class.java).apply {
            putExtra("reminderId", reminder.id)
            putExtra("reminderTitle", reminder.title)
            putExtra("reminderTime", reminder.time)
        }
        startActivityForResult(intent, ADD_REMINDER_REQUEST)
    }

    private fun deleteReminder(reminder: Reminder) {
        reminders.remove(reminder)
        saveReminders()
        reminderAdapter.notifyDataSetChanged()

    }



    companion object {
        private const val ADD_REMINDER_REQUEST = 1001
    }
}
