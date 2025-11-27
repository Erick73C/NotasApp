package com.erick.notasapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.erick.notasapp.R
import com.erick.notasapp.data.model.Repository.OfflineNotesRepository
import com.erick.notasapp.data.model.database.DatabaseProvider
import com.erick.notasapp.ui.components.NoteCard
import com.erick.notasapp.ui.utils.rememberWindowSizeClass
import com.erick.notasapp.viewmodel.NoteListViewModel
import com.erick.notasapp.viewmodel.NoteListViewModelFactory
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

import com.erick.notasapp.ui.screens.NuevaNotaScreen
import com.erick.notasapp.viewmodel.NoteViewModel
import com.erick.notasapp.viewmodel.NoteViewModelFactory
import com.erick.notasapp.data.model.Repository.MultimediaRepository
import com.erick.notasapp.data.model.dao.MultimediaDao
import com.erick.notasapp.viewmodel.MultimediaViewModel
import com.erick.notasapp.viewmodel.MultimediaViewModelFactory
import com.erick.notasapp.viewmodel.ReminderViewModel
import com.erick.notasapp.viewmodel.ReminderViewModelFactory
import com.erick.notasapp.data.model.Repository.ReminderRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NotasScreen(navController: NavController) {

    val context = LocalContext.current

    // ViewModels de notas
    val db = DatabaseProvider.provideDatabase(context)
    val notesRepo = OfflineNotesRepository(db.noteDao())
    val noteListVM: NoteListViewModel = viewModel(factory = NoteListViewModelFactory(notesRepo))

    // ViewModels para Nueva Nota
    val noteVM: NoteViewModel = viewModel(factory = NoteViewModelFactory(notesRepo))
    val multimediaRepo = MultimediaRepository(db.multimediaDao())
    val multimediaVM: MultimediaViewModel = viewModel(factory = MultimediaViewModelFactory(multimediaRepo))
    val reminderRepo = ReminderRepository(db.reminderDao())
    val reminderVM: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(reminderRepo))

    val notes by noteListVM.allNotes.collectAsState()

    val window = rememberWindowSizeClass()
    val isTablet = window.widthSizeClass >= WindowWidthSizeClass.Medium

    // Estado para seleccionar la nota en tablets
    var selectedNoteId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        floatingActionButton = {
            if (!isTablet) {
                FloatingActionButton(
                    onClick = { navController.navigate("nueva_nota") },
                    containerColor = Color(0xFFE91E63)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) { padding ->

        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFCE4EC))
        ) {

            // IZQUIERDA:Lista de notas
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {

                Text(
                    text = "Mis Notas",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD81B60),
                    modifier = Modifier.padding(bottom = 16.dp)

                )

                LazyColumn {
                    items(notes) { note ->
                        NoteCard(
                            note = note,
                            onEdit = {
                                if (isTablet) {
                                    selectedNoteId = note.id
                                } else {
                                    navController.navigate("nueva_nota/${note.id}")
                                }
                            },
                            onDelete = { noteListVM.delete(note) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            //  DERECHA: NuevaNotaScreen SOLO en tablet
            if (isTablet) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    NuevaNotaScreen(
                        navController = navController,
                        noteId = selectedNoteId,
                        noteVM = noteVM,
                        multimediaVM = multimediaVM,
                        reminderVM = reminderVM
                    )
                }
            }
        }
    }
}
