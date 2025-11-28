package com.erick.notasapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.erick.notasapp.data.AppDatabase
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(context)
                val repo = ReminderRepository(db.reminderDao())

                val reminders = repo.getAllFutureReminders()

                reminders.forEach { reminder ->
                    NotificationHelper.scheduleNotification(
                        context = context,
                        noteTitle = reminder.noteTitle,
                        noteId = reminder.noteId,
                        reminderTime = reminder.reminderTime
                    )
                }
            }
        }
    }
}
