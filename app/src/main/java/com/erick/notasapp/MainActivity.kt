package com.erick.notasapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import com.erick.notasapp.data.model.Repository.NotesRepository
import com.erick.notasapp.data.model.Repository.OfflineNotesRepository
import com.erick.notasapp.data.model.Repository.ReminderRepository
import com.erick.notasapp.data.model.database.DatabaseProvider
import com.erick.notasapp.screens.NotasScreen
import com.erick.notasapp.ui.components.Tareas
import com.erick.notasapp.ui.theme.NotasAppTheme
import com.erick.notasapp.ui.screens.ListaNotasScreen
import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.ui.screens.AjustesScreen
import com.erick.notasapp.ui.screens.LanguageManager
import com.erick.notasapp.ui.screens.Preview.AudioRecorderScreen
import com.erick.notasapp.ui.screens.Preview.PreviewImageScreen
import com.erick.notasapp.ui.screens.Preview.PreviewVideoScreen
import com.erick.notasapp.viewmodel.MultimediaViewModel
import com.erick.notasapp.viewmodel.MultimediaViewModelFactory
import com.erick.notasapp.viewmodel.NoteViewModel
import com.erick.notasapp.viewmodel.NoteViewModelFactory
import com.erick.notasapp.viewmodel.ReminderViewModel
import com.erick.notasapp.viewmodel.ReminderViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // carga idioma
        LanguageManager.loadLocale(this)

        setContent {
            val context = LocalContext.current
            // crea DB una sola vez en el scope composable
            val db = remember { DatabaseProvider.provideDatabase(context) }

            // crea repositorios una sola vez
            val noteRepo = remember { OfflineNotesRepository(db.noteDao()) }
            val reminderRepo = remember { ReminderRepository(db.reminderDao()) }
            val multimediaRepo = remember { MultimediaRepository(db.multimediaDao()) }

            // crea ViewModels UNA sola vez en este scope
            val noteVM: NoteViewModel = viewModel(factory = NoteViewModelFactory(noteRepo))
            val reminderVM: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(reminderRepo))
            val multimediaVM: MultimediaViewModel = viewModel(factory = MultimediaViewModelFactory(multimediaRepo))

            var isDarkTheme by remember { mutableStateOf(false) }
            val navController = rememberNavController()

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
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                    ) {
                        // -> PASAMOS EXACTAMENTE los parámetros que AppNavigation espera
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
                if (from == "notas") {
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
            val uri = backStackEntry.arguments?.getString("uri")?.let { Uri.parse(it) }
            if (uri != null) PreviewImageScreen(uri)
        }

        composable(
            route = "previewVideo?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->

            val raw = backStackEntry.arguments?.getString("uri")
            val uri = raw?.let { Uri.parse(it) }
            Log.d("VIDEO_URI", "Recibido: $uri")

            if (uri != null) {
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

