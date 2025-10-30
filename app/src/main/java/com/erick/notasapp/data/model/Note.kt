// Define el paquete al que pertenece esta clase.
package com.erick.notasapp.data.model

// Importa las anotaciones necesarias de la biblioteca Room.
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una entidad de nota en la base de datos.
 * Cada instancia de esta clase corresponde a una fila en la tabla "notes".
 * Room utiliza esta clase para crear la tabla y para realizar operaciones de base de datos.
 */
@Entity(tableName = "notes") // Especifica que esta clase es una entidad de base de datos y define el nombre de la tabla.
data class Note(
    @PrimaryKey(autoGenerate = true) // Marca la propiedad 'id' como la clave primaria y especifica que su valor se genera automáticamente.
    val id: Int = 0, // Identificador único para cada nota.

    @ColumnInfo(name = "title") // Especifica el nombre de la columna en la tabla para la propiedad 'title'.
    val title: String, // El título de la nota.

    @ColumnInfo(name = "description") // Especifica el nombre de la columna en la tabla para la propiedad 'description'.
    val description: String, // El contenido o descripción de la nota.

    @ColumnInfo(name = "type") // Especifica el nombre de la columna en la tabla para la propiedad 'type'.
    val type: String, // Un campo para categorizar la nota (por ejemplo, "trabajo", "personal").

    @ColumnInfo(name = "created_at") // Especifica el nombre de la columna en la tabla para la propiedad 'createdAt'.
    val createdAt: Long = System.currentTimeMillis(), // La fecha y hora de creación de la nota, en milisegundos.

    @ColumnInfo(name = "due_date") // Especifica el nombre de la columna en la tabla para la propiedad 'dueDate'.
    val dueDate: Long? = null, // La fecha de vencimiento de la nota, en milisegundos. Puede ser nulo si no hay fecha de vencimiento.

    @ColumnInfo(name = "completed") // Especifica el nombre de la columna en la tabla para la propiedad 'completed'.
    val completed: Boolean = false // Indica si la nota ha sido completada o no.
)
