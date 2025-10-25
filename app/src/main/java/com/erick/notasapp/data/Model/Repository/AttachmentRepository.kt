package com.erick.notasapp.data.Model.Repository

import com.erick.notasapp.data.Model.Attachment
import com.erick.notasapp.data.Model.dao.AttachmentDao
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar las operaciones de datos de los `Attachment` (adjuntos).
 * Funciona como una capa de abstracción entre los ViewModels y la fuente de datos, que es el `AttachmentDao`.
 * Su propósito es centralizar la lógica de acceso a los datos de los adjuntos,
 * permitiendo que el resto de la app interactúe con ellos sin conocer los detalles de la base de datos.
 */
class AttachmentRepository(private val attachmentDao: AttachmentDao) {

    /**
     * Llama al DAO para obtener un Flow con todos los adjuntos asociados a una nota específica (`noteId`).
     * Se relaciona con `AttachmentDao` para ejecutar la consulta y con el modelo `Attachment` para devolver la lista de objetos.
     */
    fun getAttachmentsByNoteId(noteId: Int): Flow<List<Attachment>> =
        attachmentDao.getAttachmentsByNoteId(noteId)

    /**
     * Llama al DAO para insertar un nuevo adjunto en la base de datos.
     * Recibe un objeto `Attachment` y lo pasa al `AttachmentDao` para su persistencia. Devuelve el ID del nuevo registro.
     */
    suspend fun insert(attachment: Attachment): Long =
        attachmentDao.insert(attachment)

    /**
     * Llama al DAO para eliminar un adjunto existente de la base de datos.
     * Recibe el objeto `Attachment` que se debe eliminar.
     */
    suspend fun delete(attachment: Attachment) =
        attachmentDao.delete(attachment)
}
