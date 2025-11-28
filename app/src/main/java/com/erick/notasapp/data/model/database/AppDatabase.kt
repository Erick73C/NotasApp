package com.erick.notasapp.data // O el paquete donde esté tu base de datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.erick.notasapp.data.model.Multimedia
import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.data.model.dao.MultimediaDao
import com.erick.notasapp.data.model.dao.NoteDao
import com.erick.notasapp.data.model.dao.ReminderDao

@Database(entities = [Note::class, Reminder::class, Multimedia::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // --- ¡AÑADE ESTAS LÍNEAS AQUÍ! ---
    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao
    abstract fun multimediaDao(): MultimediaDao
    // ------------------------------------

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notasapp_database" // El nombre de tu base de datos
                )
                    .fallbackToDestructiveMigration() // Útil para evitar errores si cambias la estructura
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
