package com.erick.notasapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NuevaNotaScreen(
    navController: NavController,
    noteId: Int? = null
) {
    val context = LocalContext.current
    val db = DatabaseProvider.provideDatabase(context)
    val repo = OfflineNotesRepository(db.noteDao())
    val factory = NoteViewModelFactory(repo)
    val viewModel: NoteViewModel = viewModel(factory = factory)

    val isDarkTheme = isSystemInDarkTheme()
    val primaryPink = if (isDarkTheme) Color(0xFFFF80AB) else Color(0xFFD81B60)
    val buttonTextColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val windowSizeClass = rememberWindowSizeClass()
    val isExpandedScreen = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    LaunchedEffect(noteId) {
        if (noteId != null) viewModel.loadNoteById(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId != null)
                            stringResource(R.string.title_editar_nota)
                        else
                            stringResource(R.string.title_nueva_nota),
                        color = buttonTextColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.salir),
                            contentDescription = stringResource(R.string.content_atras)
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
                Text(
                    text = stringResource(R.string.label_titulo),
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                OutlinedTextField(
                    value = viewModel.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.hint_titulo)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.label_descripcion),
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                OutlinedTextField(
                    value = viewModel.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text(stringResource(R.string.hint_descripcion)) }
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
                        Text(stringResource(R.string.btn_cancelar), color = textColor)
                    }

                    Button(
                        onClick = {
                            viewModel.saveNote(noteId) {
                                navController.popBackStack()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPink)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_guardar),
                            color = buttonTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MultimediaButton(iconRes: Int, texto: String, textColor: Color) {
    OutlinedButton(
        onClick = { /* agregar multimedia */ },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = texto,
                modifier = Modifier.size(24.dp)
            )
            Text(texto, fontSize = 12.sp, color = textColor)
        }
    }
}
