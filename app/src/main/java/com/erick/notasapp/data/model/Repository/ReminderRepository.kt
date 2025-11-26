package com.erick.notasapp.data.model.Repository

import com.erick.notasapp.data.model.Reminder
import com.erick.notasapp.data.model.dao.ReminderDao
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para manejar las operaciones de datos de los `Reminder` (recordatorios).
 * Sirve como intermediario entre los ViewModels y la fuente de datos, que es el `ReminderDao`.
 * Este repositorio centraliza y abstrae la lógica para acceder a los datos de los recordatorios,
 * proporcionando una API clara para que el resto de la app la consuma sin interactuar directamente con la base de datos.
 */
class ReminderRepository(private val reminderDao: ReminderDao) {

    /**
     * Llama al DAO para obtener un Flow con todos los recordatorios para una nota específica.
     * Se relaciona con `ReminderDao` para ejecutar la consulta y con el modelo `Reminder` para devolver la lista.
     */
    fun getRemindersForNote(noteId: Int): Flow<List<Reminder>> =
        reminderDao.getRemindersForNote(noteId)

    /**
     * Llama al DAO para insertar un nuevo recordatorio en la base de datos.
     * Recibe un objeto `Reminder` y lo pasa al DAO para su almacenamiento. Devuelve el ID del nuevo registro.
     */
    suspend fun insert(reminder: Reminder): Long =
        reminderDao.insert(reminder)
    suspend fun update(reminder: Reminder) = reminderDao.update(reminder)

    /**
     * Llama al DAO para eliminar un recordatorio existente de la base de datos.
     * Recibe el objeto `Reminder` que se desea eliminar.
     */
    suspend fun delete(reminder: Reminder) =
        reminderDao.delete(reminder)
}
