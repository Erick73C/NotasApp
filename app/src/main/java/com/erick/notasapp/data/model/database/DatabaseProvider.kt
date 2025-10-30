package com.erick.notasapp.data.model.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private const val DB_NAME = "app_notas_db"

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                //producción maneja migraciones correctamente
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}