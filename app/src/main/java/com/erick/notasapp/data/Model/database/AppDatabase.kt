package com.erick.notasapp.data.Model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erick.notasapp.data.Model.Attachment
import com.erick.notasapp.data.Model.Note
import com.erick.notasapp.data.Model.Reminder
import com.erick.notasapp.data.Model.dao.AttachmentDao
import com.erick.notasapp.data.Model.dao.NoteDao
import com.erick.notasapp.data.Model.dao.ReminderDao

@Database(
    entities = [Note::class, Attachment::class, Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun reminderDao(): ReminderDao
}