package com.erick.notasapp.ui.screens

import android.R.attr.textColor
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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.viewmodel.MultimediaViewModel
import com.erick.notasapp.viewmodel.MultimediaViewModelFactory
import com.erick.notasapp.data.model.dao.MultimediaDao
import com.erick.notasapp.ui.components.ReminderSection
import com.erick.notasapp.viewmodel.ReminderViewModel
import com.erick.notasapp.viewmodel.ReminderViewModelFactory
import java.io.File
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

//LAS VARIABLES SE TIENEN QUE MOVER L MIN ACTIVITY

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun NuevaNotaScreen(
    navController: NavController,
    noteId: Int? = null,
    noteVM: NoteViewModel,
    multimediaVM: MultimediaViewModel,
    reminderVM: ReminderViewModel
) {
    val context = LocalContext.current

    // Tema
    val isDark = isSystemInDarkTheme()
    val primaryPink = if (isDark) Color(0xFFFF80AB) else Color(0xFFD81B60)
    val buttonTextColor = if (isDark) Color.Black else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    LaunchedEffect(noteId) {
        if (noteId != null) {
            // EDITAR NOTA
            noteVM.loadNoteById(noteId)
            reminderVM.loadReminders(noteId)
        } else {
            // NUEVA NOTA → LIMPIAR CAMPOS
            noteVM.clearFields()
            multimediaVM.clear()
            reminderVM.clear()
        }
    }


    // CARGAR NOTA Y RECORDATORIOS
    LaunchedEffect(noteId) {
        if (noteId != null) {
            noteVM.loadNoteById(noteId)
            reminderVM.loadReminders(noteId)
        }
    }


    LaunchedEffect(reminderVM.showDatePicker) {
        if (reminderVM.showDatePicker) {
            DatePickerDialog(
                context,
                { _, y, m, d ->
                    reminderVM.onDatePicked(y, m + 1, d)
                },
                reminderVM.selectedYear,
                reminderVM.selectedMonth - 1,
                reminderVM.selectedDay
            ).apply {
                setOnCancelListener { reminderVM.hidePickers() }
                show()
            }
        }
    }

    LaunchedEffect(reminderVM.showTimePicker) {
        if (reminderVM.showTimePicker) {
            TimePickerDialog(
                context,
                { _, h, min ->
                    // llamada posicional: h y min se pasan en orden
                    reminderVM.onTimePicked(h, min)

                },
                reminderVM.selectedHour,
                reminderVM.selectedMinute,
                true
            ).apply {
                setOnCancelListener { reminderVM.hidePickers() }
                show()
            }
        }
    }

    val launcherCameraImage =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) multimediaVM.tempUri?.let { multimediaVM.addImage(it) }
        }

    val launcherPickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { multimediaVM.addImage(it) }
        }

    val launcherCameraVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) multimediaVM.tempUri?.let { multimediaVM.addVideo(it) }
        }

    val launcherPickVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { multimediaVM.addVideo(it) }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId == null)
                            stringResource(R.string.title_nueva_nota)
                        else
                            stringResource(R.string.title_editar_nota),
                        color = buttonTextColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painterResource(id = R.drawable.salir),
                            contentDescription = stringResource(R.string.content_atras)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryPink)
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            item {
                Text(stringResource(R.string.label_titulo), color = textColor)
                OutlinedTextField(
                    value = noteVM.titulo,
                    onValueChange = noteVM::onTituloChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.hint_titulo)) }
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Text(stringResource(R.string.label_descripcion), color = textColor)
                OutlinedTextField(
                    value = noteVM.descripcion,
                    onValueChange = noteVM::onDescripcionChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text(stringResource(R.string.hint_descripcion)) }
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                Text(stringResource(R.string.label_multimedia), color = textColor)

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
                                Toast.makeText(context, "Permisos requeridos", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(context, "Permisos requeridos", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },

                    onAddAudioClick = {
                        multimediaVM.checkAndRequestPermissions(
                            context,
                            onGranted = {
                                navController.navigate("audioRecorder")
                            },
                            onDenied = {
                                Toast.makeText(context, "Permiso de micrófono requerido", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },


                    onItemClick = { uri ->
                        when (context.contentResolver.getType(uri)?.substringBefore("/")) {
                            "image" -> navController.navigate("previewImage?uri=${Uri.encode(uri.toString())}")
                            "video" -> navController.navigate("previewVideo?uri=${Uri.encode(uri.toString())}")
                            "audio" -> multimediaVM.playAudio(context, uri)
                        }
                    },

                    textColor = textColor
                )

                Spacer(Modifier.height(20.dp))
            }

            item {
                Text(
                    stringResource(R.string.label_recordatorios),
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )

                reminderVM.reminders.forEach { reminder ->
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = reminder.reminderTime
                    }

                    val fecha = "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
                    val hora = "%02d:%02d".format(
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE)
                    )

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$fecha - $hora", color = textColor)

                        Row {
                            IconButton(onClick = { reminderVM.prepareEdit(reminder) }) {
                                Icon(
                                    painterResource(R.drawable.lapiz),
                                    contentDescription = stringResource(R.string.content_opciones)
                                )
                            }
                            IconButton(onClick = { reminderVM.deleteReminder(reminder) }) {
                                Icon(
                                    painterResource(R.drawable.basura),
                                    contentDescription = stringResource(R.string.content_eliminar)
                                )
                            }
                        }
                    }
                    // AQUÍ ES DONDE SE DEBE LLAMAR A AlarmManager PARA PROGRAMAR LA NOTIFICACIÓN
                    // scheduleNotification(context, reminder.reminderTime, reminder.id)
                }

                Button(
                    onClick = { reminderVM.openNewReminder() },
                    colors = ButtonDefaults.buttonColors(primaryPink)
                ) {
                    Text(stringResource(R.string.btn_agregar_recordatorio), color = buttonTextColor)
                }

                Spacer(Modifier.height(20.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text(stringResource(R.string.btn_cancelar), color = textColor)
                    }

                    Button(
                        onClick = {
                            noteVM.saveNote(noteId) { newId ->
                                val realId = noteId ?: newId

                                CoroutineScope(Dispatchers.Main).launch {
                                    reminderVM.saveAll(realId)
                                    navController.popBackStack()
                                }
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPink)
                    ) {
                        Text(stringResource(id = R.string.btn_guardar), color = buttonTextColor)
                    }
                }

                Spacer(Modifier.height(30.dp))
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


