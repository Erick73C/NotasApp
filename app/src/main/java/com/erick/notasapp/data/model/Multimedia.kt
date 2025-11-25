package com.erick.notasapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "multimedia")
data class Multimedia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noteId: Int,
    val tipo: String, // "imagen", "video", "audio"
    val uri: String,
    val descripcion: String? = null
)
