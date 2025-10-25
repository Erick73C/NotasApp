package com.erick.notasapp.data.Model.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erick.notasapp.data.Model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Objeto de Acceso a Datos (DAO) para la entidad `Note`.
 * Esta interfaz es utilizada por Room para generar el código necesario para interactuar con la tabla "notes" en la base de datos.
 * Define todos los métodos para las operaciones de base de datos (leer, escribir, actualizar, eliminar) relacionadas con las notas.
 * Cada método aquí se corresponde con una consulta o una operación en la tabla definida por la clase `Note.kt`.
 */
@Dao // Anotación que le indica a Room que esta es una interfaz DAO.
interface NoteDao {

    /**
     * Recupera todas las notas de la tabla "notes", ordenadas por fecha de creación descendente (la más nueva primero).
     * Devuelve un Flow, lo que permite a la UI observar cambios en los datos en tiempo real y actualizarse automáticamente.
     */
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotes(): Flow<List<Note>>

    /**
     * Busca y recupera una única nota de la base de datos usando su ID.
     * Es una función suspendida, por lo que debe ser llamada desde una corrutina.
     * Devuelve un objeto Note o null si no se encuentra ninguna nota con ese ID.
     */
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    /**
     * Inserta una nueva nota en la base de datos.
     * `onConflict = OnConflictStrategy.REPLACE` significa que si se intenta insertar una nota con un ID que ya existe, la nota existente será reemplazada por la nueva.
     * Devuelve el ID de la fila recién insertada como un Long.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    /**
     * Actualiza una nota existente en la base de datos.
     * Room utiliza la clave primaria (el 'id' de la nota) para encontrar y actualizar el registro correspondiente.
     */
    @Update
    suspend fun update(note: Note)

    /**
     * Elimina una nota de la base de datos.
     * Room identifica la nota a eliminar basándose en su clave primaria.
     */
    @Delete
    suspend fun delete(note: Note)

    /**
     * Busca notas cuyo título o descripción contengan el texto de la consulta (`query`).
     * La búsqueda no distingue entre mayúsculas y minúsculas gracias al operador LIKE.
     * Devuelve un Flow con la lista de notas que coinciden, actualizándose si los datos cambian.
     */
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ")
    fun searchNotes(query: String): Flow<List<Note>>

    /**
     * Actualiza el estado de completado (`completed`) de una tarea específica, identificada por su ID.
     * Esta es una forma eficiente de actualizar un solo campo sin tener que modificar todo el objeto Note.
     */
    @Query("UPDATE notes SET completed = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Int, completed: Boolean)
}
