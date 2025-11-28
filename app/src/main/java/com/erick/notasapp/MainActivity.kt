package com.erick.notasapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.core.net.toUri
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import com.erick.notasapp.data.model.Repository.OfflineNotesRepository
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.data.model.database.DatabaseProvider
import com.erick.notasapp.screens.NotasScreen
import com.erick.notasapp.ui.components.Tareas
import com.erick.notasapp.ui.theme.NotasAppTheme
import com.erick.notasapp.ui.screens.AjustesScreen
import com.erick.notasapp.ui.screens.LanguageManager
import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.ui.screens.Preview.AudioRecorderScreen
import com.erick.notasapp.ui.screens.Preview.PreviewImageScreen
import com.erick.notasapp.ui.screens.Preview.PreviewVideoScreen
import com.erick.notasapp.viewmodel.MultimediaViewModel
import com.erick.notasapp.viewmodel.MultimediaViewModelFactory
import com.erick.notasapp.viewmodel.NoteViewModel
import com.erick.notasapp.viewmodel.NoteViewModelFactory
import com.erick.notasapp.viewmodel.ReminderViewModel
import com.erick.notasapp.viewmodel.ReminderViewModelFactory
import com.erick.notasapp.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    // SOLICITUD DE PERMISO DE NOTIFICACIÓN
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                NotificationHelper.createNotificationChannel(this)
            } else {
                Log.d("MAINACTIVITY", "PERMISO DE NOTIFICACIONES DENEGADO")
            }
        }

    // NAVEGACIÓN DESDE NOTIFICACIONES
    private val noteIdFromNotification = mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageManager.loadLocale(this)

        requestNotificationPermission()

        // MANEJAR EL INTENT INICIAL SI LA APP ESTABA CERRAD
        handleIntent(intent)

        setContent {
            val context = LocalContext.current

            val db = remember { DatabaseProvider.provideDatabase(context) }
            val noteRepo = remember { OfflineNotesRepository(db.noteDao()) }
            val reminderRepo = remember { ReminderRepository(db.reminderDao()) }
            val multimediaRepo = remember { MultimediaRepository(db.multimediaDao()) }

            val noteVM: NoteViewModel = viewModel(factory = NoteViewModelFactory(noteRepo))
            val reminderVM: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(reminderRepo))
            val multimediaVM: MultimediaViewModel = viewModel(factory = MultimediaViewModelFactory(multimediaRepo))

            var isDarkTheme by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            LaunchedEffect(noteIdFromNotification.value) {
                val id = noteIdFromNotification.value
                if (id != null && id != -1) {
                    navController.navigate("nueva_nota/$id") {
                        popUpTo("notas") { inclusive = false }
                        noteIdFromNotification.value = null
                    }
                }
            }

            NotasAppTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    bottomBar = {
                        Tareas(
                            darkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            onSettingsClick = { navController.navigate("ajustes") }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavigation(
                            navController = navController,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            noteVM = noteVM,
                            multimediaVM = multimediaVM,
                            reminderVM = reminderVM
                        )
                    }
                }
            }
        }
    }

    // SOBRESCRIBIR ONNEWINTENT PARA MANEJAR NOTIFICACIONES CUANDO LA APP YA ESTÁ ABIERTA
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val noteId = intent?.getIntExtra("open_note_id", -1)
        if (noteId != null && noteId != -1) {
            noteIdFromNotification.value = noteId
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    NotificationHelper.createNotificationChannel(this)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    NotificationHelper.createNotificationChannel(this)
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            NotificationHelper.createNotificationChannel(this)
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onToggleTheme: () -> Unit,
    noteVM: NoteViewModel,
    multimediaVM: MultimediaViewModel,
    reminderVM: ReminderViewModel
) {
    NavHost(navController = navController, startDestination = "notas") {

        composable("notas") {
            NotasScreen(navController)
        }

        composable("nueva_nota") {
            val from = navController.previousBackStackEntry?.destination?.route

            LaunchedEffect(from) {
                val fromAudio = from == "audioRecorder"
                if (!fromAudio) {
                    noteVM.clearFields()
                    multimediaVM.clear()
                    reminderVM.clear()
                }
            }

            NuevaNotaScreen(
                navController = navController,
                noteVM = noteVM,
                multimediaVM = multimediaVM,
                reminderVM = reminderVM
            )
        }

        composable("nueva_nota/{noteId}") { backStackEntry ->

            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()

            LaunchedEffect(noteId) {
                val previous = navController.previousBackStackEntry?.destination?.route

                if (previous != "audioRecorder" && noteId != null) {
                    noteVM.loadNoteById(noteId)
                    multimediaVM.loadMultimediaForNote(noteId) // <-- CARGA MULTIMEDIA
                    reminderVM.loadReminders(noteId)           // <-- CARGA RECORDATORIOS
                }
                if (previous == "audioRecorder" && noteId != null) {
                    multimediaVM.updateNoteId(noteId)
                    multimediaVM.loadMultimediaForNote(noteId)
                }

            }

            NuevaNotaScreen(
                navController = navController,
                noteId = noteId,
                noteVM = noteVM,
                multimediaVM = multimediaVM,
                reminderVM = reminderVM
            )
        }

        composable("ajustes") {
            AjustesScreen(
                navController = navController,
                onToggleTheme = onToggleTheme
            )
        }

        composable(
            route = "previewImage?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")
            uriString?.toUri()?.let { uri ->
                PreviewImageScreen(uri)
            }
        }

        composable(
            route = "previewVideo?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("uri")
            raw?.toUri()?.let { uri ->
                Log.d("VIDEO_URI", "RECIBIDO: $uri")
                PreviewVideoScreen(
                    navController = navController,
                    uri = uri
                )
            }
        }

        composable("audioRecorder") {
            AudioRecorderScreen(
                navController = navController,
                multimediaVM = multimediaVM
            )
        }
    }
}