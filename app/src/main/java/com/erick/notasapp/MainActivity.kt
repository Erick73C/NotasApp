package com.erick.notasapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erick.notasapp.data.AppDatabase // <<--- IMPORTANTE: Asegúrate que la ruta a tu clase AppDatabase sea correcta
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import com.erick.notasapp.data.model.Repository.OfflineNotesRepository
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.screens.NotasScreen
import com.erick.notasapp.ui.components.Tareas
import com.erick.notasapp.ui.screens.AjustesScreen
import com.erick.notasapp.ui.screens.LanguageManager
import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.ui.screens.Preview.AudioRecorderScreen
import com.erick.notasapp.ui.screens.Preview.PreviewImageScreen
import com.erick.notasapp.ui.screens.Preview.PreviewVideoScreen
import com.erick.notasapp.ui.theme.NotasAppTheme
import com.erick.notasapp.utils.NotificationHelper
import com.erick.notasapp.viewmodel.*

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

        // MANEJAR EL INTENT INICIAL SI LA APP ESTABA CERRADA
        handleIntent(intent)

        setContent {
            val context = LocalContext.current

            val db = remember { AppDatabase.getDatabase(context) }
            val noteRepo = remember { OfflineNotesRepository(db.noteDao()) }
            val reminderRepo = remember { ReminderRepository(db.reminderDao()) }
            val multimediaRepo = remember { MultimediaRepository(db.multimediaDao()) }

            val noteVM: NoteViewModel = viewModel(factory = NoteViewModelFactory(noteRepo))
            val reminderVM: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(reminderRepo))
            val multimediaVM: MultimediaViewModel = viewModel(factory = MultimediaViewModelFactory(multimediaRepo))

            var isDarkTheme by remember { mutableStateOf(false) }

            val navController = rememberNavController()

            val noteListVM: NoteListViewModel = viewModel(factory = NoteListViewModelFactory(noteRepo))

            LaunchedEffect(noteIdFromNotification.value) {
                val id = noteIdFromNotification.value
                if (id != null && id != -1) {
                    navController.navigate("nueva_nota/$id") {
                        popUpTo("notas") { inclusive = false }
                    }
                    noteIdFromNotification.value = null
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
                            reminderVM = reminderVM,
                            noteListVM = noteListVM
                        )

                    }
                }
            }
        }
    }

    // SOBRESCRIBIR ONNEWINTENT PARA MANEJAR NOTIFICACIONES CUANDO LA APP YA ESTÁ ABIERTA
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
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
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
    reminderVM: ReminderViewModel,
    noteListVM: NoteListViewModel
) {
    NavHost(navController = navController, startDestination = "notas") {

        composable("notas") {
            NotasScreen(
                navController = navController,
                noteListVM = noteListVM,
                noteVM = noteVM,
                multimediaVM = multimediaVM,
                reminderVM = reminderVM
            )
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
                    multimediaVM.loadMultimediaForNote(noteId)
                    reminderVM.loadReminders(noteId)
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
