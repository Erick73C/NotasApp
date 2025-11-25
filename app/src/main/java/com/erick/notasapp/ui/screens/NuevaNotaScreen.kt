package com.erick.notasapp.ui.screens

import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.erick.notasapp.R
import com.erick.notasapp.data.model.Repository.OfflineNotesRepository
import com.erick.notasapp.data.model.database.DatabaseProvider
import com.erick.notasapp.viewmodel.NoteViewModel
import com.erick.notasapp.viewmodel.NoteViewModelFactory
import com.erick.notasapp.ui.utils.rememberWindowSizeClass
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.core.content.FileProvider
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import com.erick.notasapp.viewmodel.MultimediaViewModel
import com.erick.notasapp.viewmodel.MultimediaViewModelFactory
import com.erick.notasapp.data.model.dao.MultimediaDao
import java.io.File

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun NuevaNotaScreen(
    navController: NavController,
    noteId: Int? = null
) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)

    val noteRepo = OfflineNotesRepository(db.noteDao())
    val noteFactory = NoteViewModelFactory(noteRepo)
    val noteVM: NoteViewModel = viewModel(factory = noteFactory)

    val multimediaRepo = MultimediaRepository(db.multimediaDao())
    val multimediaFactory = MultimediaViewModelFactory(multimediaRepo)
    val multimediaVM: MultimediaViewModel = viewModel(factory = multimediaFactory)

    val launcherCameraImage =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                multimediaVM.tempUri?.let { multimediaVM.addImage(it) }
            }
        }

    val launcherPickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { multimediaVM.addImage(it) }
        }

    val launcherCameraVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) {
                multimediaVM.tempUri?.let { multimediaVM.addVideo(it) }
            }
        }

    val launcherPickVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { multimediaVM.addVideo(uri) }
        }

    val isDarkTheme = isSystemInDarkTheme()
    val primaryPink = if (isDarkTheme) Color(0xFFFF80AB) else Color(0xFFD81B60)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val windowSizeClass = rememberWindowSizeClass()
    val isExpandedScreen = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium


    LaunchedEffect(noteId) {
        if (noteId != null) noteVM.loadNoteById(noteId)
    }

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val permisosLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->

            val denegados = results.filter { !it.value }.keys

            if (denegados.isEmpty()) {
                Toast.makeText(context, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Se requieren permisos para usar multimedia", Toast.LENGTH_LONG).show()
            }
        }

    LaunchedEffect(Unit) {
        multimediaVM.setPermissionsLauncher(permisosLauncher)
    }

    fun getMediaType(uri: Uri): String {
        val type = context.contentResolver.getType(uri) ?: return "unknown"
        return when {
            type.startsWith("image") -> "image"
            type.startsWith("video") -> "video"
            type.startsWith("audio") -> "audio"
            else -> "unknown"
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId != null) "Editar Nota" else "Nueva Nota",
                        color = buttonTextColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.salir),
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryPink)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = if (isExpandedScreen) Alignment.Center else Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .widthIn(max = if (isExpandedScreen) 600.dp else Dp.Unspecified)
                    .padding(16.dp)
            ) {

                Text("Título", color = textColor, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = noteVM.titulo,
                    onValueChange = { noteVM.onTituloChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe un título") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Descripción", color = textColor, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = noteVM.descripcion,
                    onValueChange = { noteVM.onDescripcionChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Escribe una descripción") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                MultimediaSection(
                    imageList = multimediaVM.images.collectAsState().value,
                    videoList = multimediaVM.videos.collectAsState().value,
                    audioList = multimediaVM.audios.collectAsState().value,

                    onAddImageClick = {
                        multimediaVM.checkAndRequestPermissions(
                            context,
                            onGranted = {
                                val uri = multimediaVM.prepareTempFile(
                                    context,
                                    "photo_${System.currentTimeMillis()}.jpg"
                                )
                                multimediaVM.setTempUri(uri)
                                launcherCameraImage.launch(uri)
                            },
                            onDenied = {
                                Toast.makeText(context, "Debes otorgar permisos primero", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onAddVideoClick = {
                        multimediaVM.checkAndRequestPermissions(
                            context,
                            onGranted = {
                                val uri = multimediaVM.prepareTempFile(
                                    context,
                                    "video_${System.currentTimeMillis()}.mp4"
                                )
                                multimediaVM.setTempUri(uri)
                                launcherCameraVideo.launch(uri)
                            },
                            onDenied = {
                                Toast.makeText(context, "Permite los permisos para grabar video", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },


                    onAddAudioClick = {
                        navController.navigate("audioRecorder")
                    },

                    onItemClick = { uri ->
                        when (getMediaType(uri)) {

                            "image" -> {
                                navController.navigate("previewImage?uri=${Uri.encode(uri.toString())}")
                            }

                            "video" -> {
                                navController.navigate("previewVideo?uri=${Uri.encode(uri.toString())}")
                            }

                            "audio" -> {
                                multimediaVM.playAudio(context, uri)
                                Toast.makeText(context, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()
                            }

                            else -> {
                                Toast.makeText(context, "Archivo no reconocido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },


                            textColor = textColor
                )



                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Cancelar", color = textColor)
                    }

                    Button(
                        onClick = {
                            noteVM.saveNote(noteId) {
                                navController.popBackStack()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPink)
                    ) {
                        Text("Guardar", color = buttonTextColor)
                    }
                }
            }
        }
    }
}



@Composable
fun MultimediaSection(
    imageList: List<Uri>,
    videoList: List<Uri>,
    audioList: List<Uri>,
    onAddImageClick: () -> Unit,
    onAddVideoClick: () -> Unit,
    onAddAudioClick: () -> Unit,
    onItemClick: (Uri) -> Unit,
    textColor: Color
){
    Column(Modifier.fillMaxWidth()) {

        Text(
            "Archivos Multimedia",
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MultimediaButton(R.drawable.imagen, "Imagen", textColor, onAddImageClick)
            MultimediaButton(R.drawable.video, "Video", textColor, onAddVideoClick)
            MultimediaButton(R.drawable.microfono, "Audio", textColor, onAddAudioClick)
        }

        LazyRow {
            items(imageList) { uri ->
                ThumbnailItem(uri, "img") { onItemClick(uri) }
            }
            items(videoList) { uri ->
                ThumbnailItem(uri, "vid") { onItemClick(uri) }
            }
            items(audioList) { uri ->
                ThumbnailItem(uri, "aud") { onItemClick(uri) }
            }

        }

    }
}

@Composable
fun MultimediaButton(
    iconRes: Int,
    texto: String,
    textColor: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(90.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = texto,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(texto, fontSize = 12.sp, color = textColor)
        }
    }
}

@Composable
fun ThumbnailItem(uri: Uri, type: String, onClick: () -> Unit) {
    Column(
        Modifier
            .padding(6.dp)
            .width(90.dp)
    ) {
        Box(
            Modifier
                .size(90.dp)
                .background(Color.LightGray)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            when (type) {
                "img" -> AsyncImage(model = uri, contentDescription = null)
                "vid" -> Icon(Icons.Default.PlayArrow, contentDescription = null)
                "aud" -> Icon(Icons.Default.Build, contentDescription = null)
            }
        }
        Text(
            type.uppercase(),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


