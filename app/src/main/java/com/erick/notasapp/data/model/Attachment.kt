// Define el paquete al que pertenece esta clase.
package com.erick.notasapp.data.model

// Importa las anotaciones necesarias de la biblioteca Room.
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

// @Entity: Esta anotación convierte la clase `Attachment` en una tabla de base de datos de Room llamada "attachments".
// La sección `foreignKeys` establece una relación entre esta tabla y la tabla "notes".
// - `entity = Note::class`: Especifica que la clave externa se refiere a la entidad `Note`.
// - `parentColumns = ["id"]`: La columna en la tabla padre (`Note`) a la que se hace referencia es "id".
// - `childColumns = ["note_id"]`: La columna en esta tabla (`Attachment`) que contiene la clave externa es "note_id".
// - `onDelete = ForeignKey.CASCADE`: Si se elimina una nota, todos sus archivos adjuntos asociados también se eliminarán automáticamente.
@Entity(
    tableName = "attachments",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
// data class Attachment: Define una clase de datos para almacenar la información de un archivo adjunto.
// Las clases de datos son una forma concisa en Kotlin de crear clases que guardan datos.
// Cada propiedad de la clase representa una columna en la tabla "attachments".
data class Attachment(
    @PrimaryKey(autoGenerate = true) // Marca 'id' como la clave primaria de la tabla. `autoGenerate = true` hace que Room genere un ID único para cada nuevo adjunto.
    val id: Int = 0,

    @ColumnInfo(name = "note_id") // Define una columna llamada "note_id" que almacenará el ID de la nota a la que pertenece este adjunto.
    val noteId: Int,

    @ColumnInfo(name = "file_uri") // Define una columna llamada "file_uri" para guardar la ruta (URI) del archivo adjunto.
    val fileUri: String, // Ruta o URI del archivo multimedia

    @ColumnInfo(name = "file_type") // Define una columna "file_type" para especificar el tipo de archivo (ej. "image", "video").
    val fileType: String, // "image", "video", "audio"

    @ColumnInfo(name = "description") // Define una columna "description" para una descripción opcional del adjunto.
    val description: String? = null // Puede ser nulo si no hay descripción.
)
