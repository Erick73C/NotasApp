package com.erick.notasapp.data.model.Repository

import com.erick.notasapp.data.model.Note
import com.erick.notasapp.data.model.dao.NoteDao
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del repositorio que utiliza la base de datos local (Room)
 * para almacenar y recuperar notas.
 *
 * Si en el futuro se requiere sincronización con la nube, se puede crear
 * una clase OnlineNotesRepository que implemente la misma interfaz NotesRepository.
 */
class OfflineNotesRepository(private val noteDao: NoteDao) : NotesRepository {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    override fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    override suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    override suspend fun insert(note: Note): Long = noteDao.insert(note)

    override suspend fun update(note: Note) = noteDao.update(note)

    override suspend fun delete(note: Note) = noteDao.delete(note)

    override suspend fun setTaskCompleted(id: Int, completed: Boolean) =
        noteDao.setTaskCompleted(id, completed)
}
