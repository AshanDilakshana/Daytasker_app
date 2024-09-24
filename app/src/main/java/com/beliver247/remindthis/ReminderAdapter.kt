package com.beliver247.remindthis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val reminders: List<Reminder>,
    private val onEdit: (Reminder) -> Unit,
    private val onDelete: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.reminder_title)
        val timeTextView: TextView = itemView.findViewById(R.id.reminder_time)
        val editButton: Button = itemView.findViewById(R.id.btn_edit)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]

        // Bind the title and formatted time
        holder.titleTextView.text = reminder.title
        holder.timeTextView.text = formatTime(reminder.time)

        // Set click listeners for Edit and Delete buttons
        holder.editButton.setOnClickListener {
            onEdit(reminder)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(reminder)
        }
    }

    override fun getItemCount() = reminders.size

    // Function to format the time in "HH:mm" format
    private fun formatTime(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis // No reassignment of timeInMillis, only using it to set the calendar's time

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute) // Formatting the time as HH:mm
    }
}
