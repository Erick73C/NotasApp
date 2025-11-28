package com.erick.notasapp.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erick.notasapp.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // CONSULTA PARA OBTENER RECORDATORIOS POR NOTA
    @Query("SELECT * FROM reminders WHERE note_id = :noteId ORDER BY reminder_time ASC")
    fun getRemindersForNote(noteId: Int): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    // RECUPERA TODOS LOS RECORDATORIOS CUYO TIEMPO ES MAYOR AL TIEMPO ACTUAL
    @Query("SELECT * FROM reminders WHERE reminder_time > :currentTime")
    suspend fun getAllFutureReminders(currentTime: Long): List<Reminder>
}