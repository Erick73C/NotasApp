package com.erick.notasapp.data.Model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.erick.notasapp.data.Model.Attachment
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Objeto de Acceso a Datos (DAO) para la entidad `Attachment`.
 * Esta interfaz es utilizada por Room para generar el código que interactúa con la tabla "attachments" de la base de datos.
 * Define los métodos para realizar operaciones sobre los archivos adjuntos, cuya estructura está definida en `Attachment.kt`.
 */
@Dao // Anotación que le indica a Room que esta es una interfaz DAO.
interface AttachmentDao {

    /**
     * Recupera todos los archivos adjuntos asociados a una nota específica, identificada por su `noteId`.
     * Devuelve un Flow, permitiendo que la UI observe cambios en los adjuntos de la nota en tiempo real.
     */
    @Query("SELECT * FROM attachments WHERE note_id = :noteId")
    fun getAttachmentsByNoteId(noteId: Int): Flow<List<Attachment>>

    /**
     * Inserta un nuevo archivo adjunto en la base de datos.
     * `onConflict = OnConflictStrategy.REPLACE` indica que si ya existe un adjunto con la misma clave primaria, será reemplazado.
     * Devuelve el ID de la fila recién insertada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachment: Attachment): Long

    /**
     * Elimina un archivo adjunto de la base de datos.
     * Room identifica el adjunto a eliminar basándose en su clave primaria.
     */
    @Delete
    suspend fun delete(attachment: Attachment)
}
