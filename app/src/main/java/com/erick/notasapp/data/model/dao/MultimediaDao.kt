package com.erick.notasapp.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.erick.notasapp.data.model.Multimedia

@Dao
interface MultimediaDao {

    @Insert
    suspend fun insert(media: Multimedia)

    @Delete
    suspend fun delete(media: Multimedia)

    @Query("SELECT * FROM multimedia WHERE noteId = :id")
    suspend fun getByNoteId(id: Int): List<Multimedia>
}
