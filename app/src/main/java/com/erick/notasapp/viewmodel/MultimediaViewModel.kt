package com.erick.notasapp.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erick.notasapp.data.model.Multimedia
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MultimediaViewModel(
    private val repository: MultimediaRepository
) : ViewModel() {

    // StateFlows para UI
    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images = _images.asStateFlow()

    private val _videos = MutableStateFlow<List<Uri>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _audios = MutableStateFlow<List<Uri>>(emptyList())
    val audios = _audios.asStateFlow()

    // Permisos / temp uris
    var permisosLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = null
    var tempUri: Uri? = null
        private set

    fun setTempUri(uri: Uri) { tempUri = uri }

    fun setPermissionsLauncher(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>) {
        permisosLauncher = launcher
    }
    var currentNoteId: Int? = null
    fun updateNoteId(id: Int?) {
        currentNoteId = id
    }


    fun requestPermissions() {
        val permisos = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO
        )
        permisosLauncher?.launch(permisos)
    }

    fun onPermissionsResult(
        result: Map<String, Boolean>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (result.all { it.value }) {
            onGranted()
        } else {
            onDenied()
        }
    }


    fun checkAndRequestPermissions(
        context: Context,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val permisos = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )

        val faltantes = permisos.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (faltantes.isEmpty()) {
            onGranted()    // Ya tiene permisos → ejecutar acción (abrir cámara)
        } else {
            // Pedir permisos sin ejecutar acción todavía
            permisosLauncher?.launch(faltantes.toTypedArray())
            // Aquí NO llames onDenied, eso solo se hace si realmente se negó
        }
    }


    // ---------------- TEMP FILE helpers ----------------
    fun prepareTempFile(context: Context, fileName: String): Uri {
        val folder = when {
            fileName.endsWith(".jpg") -> Environment.DIRECTORY_PICTURES
            fileName.endsWith(".mp4") -> Environment.DIRECTORY_MOVIES
            fileName.endsWith(".m4a") -> Environment.DIRECTORY_MUSIC
            else -> Environment.DIRECTORY_DOCUMENTS
        }
        val dir = context.getExternalFilesDir(folder)
        val file = File(dir, fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun prepareAudioFile(context: Context): Uri {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(dir, "audio_${System.currentTimeMillis()}.m4a")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // ---------------- in-memory lists ----------------
    fun addImage(uri: Uri) { _images.value = _images.value + uri }
    fun addVideo(uri: Uri) { _videos.value = _videos.value + uri }
    fun addAudio(uri: Uri) { _audios.value = _audios.value + uri }

    fun clear() {
        _images.value = emptyList()
        _videos.value = emptyList()
        _audios.value = emptyList()
        tempUri = null
    }

    // ---------------- recording ----------------
    private var mediaRecorder: MediaRecorder? = null
    var isRecording by mutableStateOf(false)
    private var audioTempUri: Uri? = null

    fun startRecording(context: Context) {
        audioTempUri = prepareAudioFile(context)

        // Usamos archivo físico vía file descriptor
        val pfd = context.contentResolver.openFileDescriptor(audioTempUri!!, "rw")
        val fd = pfd?.fileDescriptor

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            if (fd != null) setOutputFile(fd)
            prepare()
            start()
        }
        isRecording = true
    }

    fun stopRecording(): Uri? {
        if (!isRecording) return null

        try {
            mediaRecorder?.apply {
                try { stop() } catch (_: Exception) {}
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
            isRecording = false

            val savedUri = audioTempUri
            audioTempUri = null

            savedUri?.let { uri ->
                // 1) Agregar a lista para UI inmediata
                addAudio(uri)

                // 2) Guardar en DB
                val idNota = currentNoteId
                if (idNota != null) {
                    viewModelScope.launch {
                        repository.insert(
                            Multimedia(
                                noteId = idNota,
                                tipo = "audio",
                                uri = uri.toString()
                            )
                        )

                        // 3) Refrescar la multimedia de la nota
                        loadMultimediaForNote(idNota)
                    }
                }
            }

            return savedUri
        }
    }


    fun playAudio(context: Context, uri: Uri) {
        val player = MediaPlayer()
        player.setDataSource(context, uri)
        player.prepare()
        player.start()
    }

    // ---------------- DB operations ----------------
    // Carga la multimedia existente de la BD y la pone en los StateFlows
    fun loadMultimediaForNote(noteId: Int) {
        viewModelScope.launch {
            val list = repository.getByNoteId(noteId)
            // tu entidad tiene 'tipo' y 'uri'
            _images.value = list.filter { it.tipo == "imagen" }.map { Uri.parse(it.uri) }
            _videos.value = list.filter { it.tipo == "video" }.map { Uri.parse(it.uri) }
            _audios.value = list.filter { it.tipo == "audio" }.map { Uri.parse(it.uri) }
        }
    }

    // Guarda las listas actuales en la BD (usa Multimedia entity, no parámetros sueltos)
    fun saveMultimedia(noteId: Int) {
        viewModelScope.launch {
            images.value.forEach { uri ->
                repository.insert(
                    Multimedia(
                        noteId = noteId,
                        tipo = "imagen",
                        uri = uri.toString()
                    )
                )
            }
            videos.value.forEach { uri ->
                repository.insert(
                    Multimedia(
                        noteId = noteId,
                        tipo = "video",
                        uri = uri.toString()
                    )
                )
            }
            audios.value.forEach { uri ->
                repository.insert(
                    Multimedia(
                        noteId = noteId,
                        tipo = "audio",
                        uri = uri.toString()
                    )
                )
            }
        }
    }

    suspend fun deleteMultimediaFromNote(noteId: Int) {
        val list = repository.getByNoteId(noteId)
        list.forEach { repository.delete(it) }
    }

    // permisos helper (ya la tenías)
    fun hasPermissions(context: Context): Boolean {
        val permisos = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO
        )
        return permisos.all {
            ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }


}