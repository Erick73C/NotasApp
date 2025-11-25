package com.erick.notasapp.data.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erick.notasapp.data.model.Attachment
import com.erick.notasapp.data.model.Multimedia
import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.data.model.dao.AttachmentDao
import com.erick.notasapp.data.model.dao.MultimediaDao
import com.erick.notasapp.data.model.dao.NoteDao
import com.erick.notasapp.data.model.dao.ReminderDao

@Database(
    entities = [Note::class, Attachment::class, Reminder::class, Multimedia::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun reminderDao(): ReminderDao
    abstract fun multimediaDao(): MultimediaDao
}