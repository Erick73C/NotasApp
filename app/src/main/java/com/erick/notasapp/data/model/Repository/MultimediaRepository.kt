package com.erick.notasapp.data.model.Repository

import com.erick.notasapp.data.model.Multimedia
import com.erick.notasapp.data.model.dao.MultimediaDao

class MultimediaRepository(private val dao: MultimediaDao) {

    suspend fun insert(media: Multimedia) = dao.insert(media)

    suspend fun delete(media: Multimedia) = dao.delete(media)

    suspend fun getByNoteId(noteId: Int) = dao.getByNoteId(noteId)
}
