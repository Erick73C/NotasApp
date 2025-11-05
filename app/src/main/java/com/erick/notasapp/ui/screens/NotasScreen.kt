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
import com.erick.notasapp.viewmodel.NoteListViewModel
import com.erick.notasapp.viewmodel.NoteListViewModelFactory
import com.erick.notasapp.ui.utils.rememberWindowSizeClass
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NotasScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val repo = OfflineNotesRepository(noteDao = db.noteDao())
    val factory = NoteListViewModelFactory(repo)
    val viewModel: NoteListViewModel = viewModel(factory = factory)

    val notes by viewModel.allNotes.collectAsState()
    val windowSizeClass = rememberWindowSizeClass()
    val isExpandedScreen = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nueva_nota") },
                containerColor = Color(0xFFE91E63)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_agregar),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFCE4EC))
        ) {
            // En pantallas grandes: mostrar NavigationRail lateral
            if (isExpandedScreen) {
                NavigationRail {
                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate("nueva_nota") },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Nueva nota") },
                        label = { Text("Agregar") }
                    )
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (isExpandedScreen) 32.dp else 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_app_notas),
                    fontSize = if (isExpandedScreen) 28.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD81B60),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (notes.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_notas),
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onEdit = { navController.navigate("nueva_nota/${note.id}") },
                                onDelete = { viewModel.delete(note) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
