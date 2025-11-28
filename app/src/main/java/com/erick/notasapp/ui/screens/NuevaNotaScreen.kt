package com.erick.notasapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.erick.notasapp.R
import com.erick.notasapp.ui.components.AudioPlayerItem
import com.erick.notasapp.ui.utils.rememberWindowSizeClass
import com.erick.notasapp.utils.NotificationHelper
import com.erick.notasapp.viewmodel.MultimediaViewModel
import com.erick.notasapp.viewmodel.NoteViewModel
import com.erick.notasapp.viewmodel.ReminderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

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

    // CREANOTIFICACIÓN
    LaunchedEffect(Unit) {
        NotificationHelper.createNotificationChannel(context)
    }

    // TAMAÑO DE PANTALLA
    val windowSize = rememberWindowSizeClass()
    val isTablet = windowSize.widthSizeClass >= WindowWidthSizeClass.Medium

    // TEMA
    val isDark = isSystemInDarkTheme()
    val primaryPink = if (isDark) Color(0xFFFF80AB) else Color(0xFFD81B60)
    val buttonTextColor = if (isDark) Color.Black else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    LaunchedEffect(noteId) {
        if (noteId != null) {
            noteVM.loadNoteById(noteId)
            reminderVM.loadReminders(noteId)
            multimediaVM.updateNoteId(noteId)
            multimediaVM.loadMultimediaForNote(noteId)
        } else {
            noteVM.clearFields()
            multimediaVM.clear()
            reminderVM.clear()
            multimediaVM.updateNoteId(null)
        }
    }

    // DATE PICKER
    LaunchedEffect(reminderVM.showDatePicker) {
        if (reminderVM.showDatePicker) {
            DatePickerDialog(
                context,
                { _, y, m, d -> reminderVM.onDatePicked(y, m + 1, d) },
                reminderVM.selectedYear,
                reminderVM.selectedMonth - 1,
                reminderVM.selectedDay
            ).apply {
                setOnCancelListener { reminderVM.hidePickers() }
                show()
            }
        }
    }

    // TIME PICKER
    LaunchedEffect(reminderVM.showTimePicker) {
        if (reminderVM.showTimePicker) {
            TimePickerDialog(
                context,
                { _, h, min -> reminderVM.onTimePicked(h, min) },
                reminderVM.selectedHour,
                reminderVM.selectedMinute,
                true
            ).apply {
                setOnCancelListener { reminderVM.hidePickers() }
                show()
            }
        }
    }

    // LAUNCHERS
    val launcherCameraImage =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) multimediaVM.tempUri?.let { multimediaVM.addImage(it) }
        }

    val launcherCameraVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            if (success) multimediaVM.tempUri?.let { multimediaVM.addVideo(it) }
        }

    val launcherPickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { multimediaVM.addImage(it) }
        }

    val launcherPickVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { multimediaVM.addVideo(it) }
        }

    val permissionsLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            multimediaVM.onPermissionsResult(
                result,
                onGranted = {
                    Toast.makeText(context, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
                },
                onDenied = {
                    Toast.makeText(context, "SE REQUIEREN PERMISOS PARA MULTIMEDIA", Toast.LENGTH_LONG).show()
                }
            )
        }

    LaunchedEffect(Unit) {
        multimediaVM.setPermissionsLauncher(permissionsLauncher)
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

        if (isTablet) {
            // ================= MODO TABLET =================
            Row(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // IZQUIERDA
                Column(
                    Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {

                    Text(stringResource(R.string.label_titulo), color = textColor)
                    OutlinedTextField(
                        value = noteVM.titulo,
                        onValueChange = noteVM::onTituloChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(stringResource(R.string.label_descripcion), color = textColor)
                    OutlinedTextField(
                        value = noteVM.descripcion,
                        onValueChange = noteVM::onDescripcionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(Modifier.height(20.dp))

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
                                    Toast.makeText(context, "PERMISOS REQUERIDOS", Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(context, "PERMISOS REQUERIDOS", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onAddAudioClick = {
                            multimediaVM.checkAndRequestPermissions(
                                context,
                                onGranted = { navController.navigate("audioRecorder") },
                                onDenied = {
                                    Toast.makeText(context, "PERMISO DE MICRÓFONO REQUERIDO", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onItemClick = { uri: Uri ->
                            val path = uri.toString()
                            when {
                                path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") ->
                                    navController.navigate("previewImage?uri=${Uri.encode(uri.toString())}")
                                path.endsWith(".mp4") || path.endsWith(".mov") ->
                                    navController.navigate("previewVideo?uri=${Uri.encode(uri.toString())}")
                                path.endsWith(".m4a") || path.endsWith(".aac") || path.endsWith(".mp3") ->
                                    multimediaVM.playAudio(context, uri)
                                else ->
                                    Toast.makeText(context, "ARCHIVO NO RECONOCIDO", Toast.LENGTH_SHORT).show()
                            }
                        },
                        textColor = textColor
                    )
                }

                // DERECHA
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {

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
                                    Icon(painterResource(R.drawable.lapiz), null)
                                }
                                IconButton(onClick = { reminderVM.deleteReminder(reminder) }) {
                                    Icon(painterResource(R.drawable.basura), null)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { reminderVM.openNewReminder() },
                        colors = ButtonDefaults.buttonColors(primaryPink)
                    ) {
                        Text(stringResource(R.string.btn_agregar_recordatorio), color = buttonTextColor)
                    }

                    Spacer(Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(onClick = { navController.popBackStack() }) {
                            Text(stringResource(R.string.btn_cancelar), color = textColor)
                        }

                        // BOTÓN GUARDAR ALARMA
                        Button(
                            onClick = {
                                noteVM.saveNote(noteId) { newId ->
                                    val realId = noteId ?: newId
                                    CoroutineScope(Dispatchers.Main).launch {

                                        // GUARDA RECORDATORIOS
                                        reminderVM.saveAll(realId)

                                        // PROGRAMA LAS ALARMAS
                                        reminderVM.reminders.forEach { reminder ->

                                            // CANCELA CUALQUIER ALARMA ANTERIOR PARA ESTA NOTA
                                            NotificationHelper.cancelNotification(context, realId)

                                            // SOLO PROGRAMA SI LA HORA ES EN EL FUTURO
                                            if (reminder.reminderTime > System.currentTimeMillis()) {
                                                NotificationHelper.scheduleNotification(
                                                    context = context,
                                                    noteTitle = noteVM.titulo.ifEmpty { "NOTA SIN TÍTULO" },
                                                    noteId = realId,
                                                    reminderTime = reminder.reminderTime
                                                )
                                            }
                                        }

                                        multimediaVM.saveMultimedia(realId)

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
                }
            }

        } else {
            // ================= MODO MÓVIL =================
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(stringResource(R.string.label_titulo), color = textColor)
                    OutlinedTextField(
                        value = noteVM.titulo,
                        onValueChange = noteVM::onTituloChange,
                        modifier = Modifier.fillMaxWidth()
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
                            .height(120.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                }

                item {
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
                                    Toast.makeText(context, "PERMISOS REQUERIDOS", Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(context, "PERMISOS REQUERIDOS", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onAddAudioClick = {
                            multimediaVM.checkAndRequestPermissions(
                                context,
                                onGranted = {
                                    multimediaVM.updateNoteId(noteId)
                                    navController.navigate("audioRecorder")
                                },
                                onDenied = {
                                    Toast.makeText(context, "PERMISO DE MICRÓFONO REQUERIDO", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onItemClick = { uri: Uri ->
                            val path = uri.toString()
                            when {
                                path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") ->
                                    navController.navigate("previewImage?uri=${Uri.encode(uri.toString())}")
                                path.endsWith(".mp4") || path.endsWith(".mov") ->
                                    navController.navigate("previewVideo?uri=${Uri.encode(uri.toString())}")
                                path.endsWith(".m4a") || path.endsWith(".aac") || path.endsWith(".mp3") ->
                                    multimediaVM.playAudio(context, uri)
                                else ->
                                    Toast.makeText(context, "TIPO NO RECONOCIDO", Toast.LENGTH_SHORT).show()
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
                                        contentDescription = null
                                    )
                                }
                                IconButton(onClick = { reminderVM.deleteReminder(reminder) }) {
                                    Icon(
                                        painterResource(R.drawable.basura),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
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

                        // BOTÓN GUARDA ALARMAS
                        Button(
                            onClick = {
                                noteVM.saveNote(noteId) { newId ->
                                    val realId = noteId ?: newId
                                    CoroutineScope(Dispatchers.Main).launch {

                                        // GUARDAR RECORDATORIOS
                                        reminderVM.saveAll(realId)

                                        // PROGRAMA LAS ALARMAS
                                        reminderVM.reminders.forEach { reminder ->

                                            // CANCELA CUALQUIER ALARMA ANTERIOR
                                            NotificationHelper.cancelNotification(context, realId)

                                            // PROGRAMA SI LA HORA ES EN EL FUTURO
                                            if (reminder.reminderTime > System.currentTimeMillis()) {
                                                NotificationHelper.scheduleNotification(
                                                    context = context,
                                                    noteTitle = noteVM.titulo.ifEmpty { "NOTA SIN TÍTULO" },
                                                    noteId = realId,
                                                    reminderTime = reminder.reminderTime
                                                )
                                            }
                                        }

                                        multimediaVM.saveMultimedia(realId)

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
) {
    Column(Modifier.fillMaxWidth()) {

        Text(
            "ARCHIVOS MULTIMEDIA",
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
            MultimediaButton(R.drawable.imagen, "IMAGEN", textColor, onAddImageClick)
            MultimediaButton(R.drawable.video, "VIDEO", textColor, onAddVideoClick)
            MultimediaButton(R.drawable.microfono, "AUDIO", textColor, onAddAudioClick)
        }

        LazyRow {
            items(imageList) { uri ->
                ThumbnailItem(uri, "IMG") { onItemClick(uri) }
            }
            items(videoList) { uri ->
                ThumbnailItem(uri, "VID") { onItemClick(uri) }
            }
            items(audioList) { uri ->
                AudioPlayerItem(uri)
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
                "IMG" -> AsyncImage(model = uri, contentDescription = null)
                "VID" -> Icon(Icons.Default.PlayArrow, contentDescription = null)
                "AUD" -> Icon(Icons.Default.Build, contentDescription = null)
            }
        }
        Text(
            type.uppercase(),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}