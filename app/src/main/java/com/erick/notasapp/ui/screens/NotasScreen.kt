package com.erick.notasapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.erick.notasapp.R
import com.erick.notasapp.data.model.Repository.NoteRepository
import com.erick.notasapp.data.model.database.DatabaseProvider
import com.erick.notasapp.ui.components.NoteCard
import com.erick.notasapp.viewmodel.NoteViewModel
import com.erick.notasapp.viewmodel.NoteViewModelFactory

@Composable
fun NotasScreen(navController: NavController) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val repo = NoteRepository(db.noteDao())
    val factory = NoteViewModelFactory(repo)
    val viewModel: NoteViewModel = viewModel(factory = factory)

    val notes by viewModel.allNotes.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val paddingHorizontal = when {
        screenWidth < 360 -> 8.dp
        screenWidth < 600 -> 16.dp
        else -> 32.dp
    }

    val titleFontSize = when {
        screenWidth < 360 -> 18.sp
        screenWidth < 600 -> 22.sp
        else -> 28.sp
    }

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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = paddingHorizontal, vertical = 16.dp)
                .fillMaxSize()
                .background(Color(0xFFFCE4EC))
        ) {
            Text(
                text = stringResource(R.string.title_app_notas),
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD81B60),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (notes.isEmpty()) {
                Text(
                    text = "No hay notas aún. Presiona + para crear una nueva.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onEdit = { selectedNote ->
                                navController.navigate("nueva_nota/${selectedNote.id}")
                            },
                            onDelete = { selectedNote ->
                                viewModel.delete(selectedNote)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}