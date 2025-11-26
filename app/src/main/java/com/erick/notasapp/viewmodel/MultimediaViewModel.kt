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
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class MultimediaViewModel(
    private val repository: MultimediaRepository
) : ViewModel() {

    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    var permisosLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = null


    private val _videos = MutableStateFlow<List<Uri>>(emptyList())
    val videos: StateFlow<List<Uri>> = _videos

    private val _audios = MutableStateFlow<List<Uri>>(emptyList())
    val audios: StateFlow<List<Uri>> = _audios

    var tempUri: Uri? = null
        private set

    fun setTempUri(uri: Uri) {
        tempUri = uri
    }

    fun setPermissionsLauncher(
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
    ) {
        permisosLauncher = launcher
    }

    fun requestPermissions() {
        val permisos = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )

        permisosLauncher?.launch(permisos)
    }

    fun checkAndRequestPermissions(
        context: Context,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (hasPermissions(context)) {
            onGranted()
        } else {
            onDenied()
            requestPermissions()
        }
    }


    /**
     * Crea un archivo temporal PARA CUALQUIER TIPO DE MULTIMEDIA.
     * Puede ser fotos, videos, audio, etc.
     */
    fun createTempUri(
        context: Context,
        fileName: String,
        directory: String
    ): Uri {

        val dir = context.getExternalFilesDir(directory)
        val file = File(dir, fileName)

        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }
    fun addImage(uri: Uri) {
        _images.value = _images.value + uri
    }

    fun addVideo(uri: Uri) {
        _videos.value = _videos.value + uri
    }

    fun addAudio(uri: Uri) {
        _audios.value = _audios.value + uri
    }

    var mediaRecorder: MediaRecorder? = null
    var isRecording by mutableStateOf(false)
    private var audioTempUri: Uri? = null
    fun prepareTempFile(context: Context, fileName: String): Uri {
        val folder = when {
            fileName.endsWith(".jpg") -> Environment.DIRECTORY_PICTURES
            fileName.endsWith(".mp4") -> Environment.DIRECTORY_MOVIES
            fileName.endsWith(".m4a") -> Environment.DIRECTORY_MUSIC
            else -> Environment.DIRECTORY_DOCUMENTS
        }

        val dir = context.getExternalFilesDir(folder)
        val file = File(dir, fileName)

        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }


    fun prepareAudioFile(context: Context): Uri {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(dir, "audio_${System.currentTimeMillis()}.m4a")
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }

    fun startRecording(context: Context) {
        audioTempUri = prepareAudioFile(context)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(
                context.contentResolver.openFileDescriptor(audioTempUri!!, "rw")!!.fileDescriptor
            )
            prepare()
            start()
        }

        isRecording = true
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false

        audioTempUri?.let {
            addAudio(it)
        }
    }


    fun playAudio(context: Context, uri: Uri) {
        val player = MediaPlayer()
        player.setDataSource(context, uri)
        player.prepare()
        player.start()
    }

    fun hasPermissions(context: Context): Boolean {
        val permisos = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )

        return permisos.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun clear() {
        _images.value = emptyList()
        _videos.value = emptyList()
        _audios.value = emptyList()
        tempUri = null
    }


}

