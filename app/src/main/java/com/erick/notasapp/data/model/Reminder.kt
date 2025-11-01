// Define el paquete al que pertenece esta clase.
package com.erick.notasapp.data.model

// Importa las anotaciones necesarias de la biblioteca Room.
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

// @Entity: Esta anotación convierte la clase `Reminder` en una tabla de base de datos de Room llamada "reminders".
// La sección `foreignKeys` establece una relación entre esta tabla y la tabla "notes".
// - `entity = Note::class`: Especifica que la clave externa se refiere a la entidad `Note`.
// - `parentColumns = ["id"]`: La columna en la tabla padre (`Note`) a la que se hace referencia es "id".
// - `childColumns = ["note_id"]`: La columna en esta tabla (`Reminder`) que contiene la clave externa es "note_id".
// - `onDelete = ForeignKey.CASCADE`: Si se elimina una nota, su recordatorio asociado también se eliminará automáticamente.
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
// data class Reminder: Define una clase de datos para almacenar la información de un recordatorio.
// Las clases de datos son una forma concisa en Kotlin de crear clases que principalmente guardan datos.
// Cada propiedad de la clase representa una columna en la tabla "reminders".
data class Reminder(
    // @PrimaryKey: Marca 'id' como la clave primaria de la tabla.
    // `autoGenerate = true` hace que Room genere un ID único para cada nuevo recordatorio.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Identificador único para cada recordatorio.

    // @ColumnInfo: Define una columna llamada "note_id" que almacenará el ID de la nota a la que pertenece este recordatorio.
    @ColumnInfo(name = "note_id")
    val noteId: Int, // El ID de la nota asociada.

    // @ColumnInfo: Define una columna llamada "reminder_time" para guardar la fecha y hora del recordatorio.
    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long // La fecha y hora del recordatorio, usualmente en formato de milisegundos (timestamp).
)
