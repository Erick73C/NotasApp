package com.erick.notasapp.data.model.Repository

import com.erick.notasapp.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define las operaciones básicas de acceso a datos para las notas.
 * Esto permite cambiar fácilmente la fuente de datos (local, remota o combinada).
 */
interface NotesRepository {

    fun getAllNotes(): Flow<List<Note>>

    fun searchNotes(query: String): Flow<List<Note>>

    suspend fun getNoteById(id: Int): Note?

    suspend fun insert(note: Note): Long

    suspend fun update(note: Note)

    suspend fun delete(note: Note)

    suspend fun setTaskCompleted(id: Int, completed: Boolean)
}