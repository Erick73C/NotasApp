package com.erick.notasapp.data.Model.Repository

import com.erick.notasapp.data.Model.Note
import com.erick.notasapp.data.Model.dao.NoteDao
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para manejar las operaciones de datos de las `Note`.
 * Actúa como intermediario entre los ViewModels y la fuente de datos (el `NoteDao`).
 * Abstrae el origen de los datos (base de datos Room) del resto de la aplicación,
 * proporcionando una API limpia y clara para acceder y modificar los datos de las notas.
 */
class NoteRepository(private val noteDao: NoteDao) {

    /**
     * Llama al DAO para obtener un Flow con todas las notas de la base de datos, ordenadas por fecha.
     */
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    /**
     * Llama al DAO para obtener una única nota por su ID. Es una función suspendida.
     */
    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    /**
     * Llama al DAO para insertar una nueva nota en la base de datos. Devuelve el ID de la nota insertada.
     */
    suspend fun insert(note: Note): Long = noteDao.insert(note)

    /**
     * Llama al DAO para actualizar los datos de una nota existente.
     */
    suspend fun update(note: Note) = noteDao.update(note)

    /**
     * Llama al DAO para eliminar una nota de la base de datos.
     */
    suspend fun delete(note: Note) = noteDao.delete(note)

    /**
     * Llama al DAO para buscar notas que coincidan con un texto en su título o descripción.
     */
    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    /**
     * Llama al DAO para actualizar el estado 'completado' de una nota específica.
     */
    suspend fun setTaskCompleted(id: Int, completed: Boolean) = noteDao.setTaskCompleted(id, completed)
}
