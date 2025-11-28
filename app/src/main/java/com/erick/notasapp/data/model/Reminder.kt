package com.erick.notasapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "note_id")
    val noteId: Int,

    //muestra la notificación
    @ColumnInfo(name = "note_title")
    val noteTitle: String,

    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long
)