package com.erick.notasapp.data.model.database

import android.content.Context
import androidx.room.Room

//Encargado de crear y proveer una única instancia de la base de datos (AppDatabase) en toda la aplicación.
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
               // .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}