package com.erick.notasapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import com.erick.notasapp.R
import kotlin.math.min

@Composable
fun Tareas(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Área izquierda
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.task_bar),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            }

            // Botones a la derecha
            Row(
                modifier = Modifier.padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de ajustes
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    SettingsIcon(Color.Gray, 20.dp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón de modo oscuro/claro
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = if (darkTheme) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    val fg = if (darkTheme) Color.White else MaterialTheme.colorScheme.primary
                    SunMoonIcon(isMoon = darkTheme, size = 20.dp, tint = fg)
                }
            }
        }
    }
}

@Composable
fun SunMoonIcon(isMoon: Boolean, size: Dp, tint: Color) {
    Canvas(modifier = Modifier.size(size)) {
        if (isMoon) drawMoon(tint) else drawSun(tint)
    }
}

private fun DrawScope.drawSun(tint: Color) {
    val canvasWidth = this.size.width
    val canvasHeight = this.size.height
    val r = min(canvasWidth, canvasHeight) / 4f

    drawCircle(color = tint, radius = r, center = center)
    val rayLength = r * 1.8f
    for (i in 0 until 8) {
        rotate(degrees = i * 45f, pivot = center) {
            drawLine(
                color = tint,
                start = center,
                end = center.copy(y = center.y - rayLength),
                strokeWidth = 2f
            )
        }
    }
}

private fun DrawScope.drawMoon(tint: Color) {
    val canvasWidth = this.size.width
    val canvasHeight = this.size.height
    val r = min(canvasWidth, canvasHeight) / 4f

    drawCircle(color = tint, radius = r, center = center)
    drawCircle(
        color = Color.Transparent,
        radius = r * 0.7f,
        center = center.copy(x = center.x + r / 2)
    )
}

@Composable
fun SettingsIcon(tint: Color, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height
        val r = min(canvasWidth, canvasHeight) / 4f

        drawCircle(color = tint, radius = r, center = center)
        for (i in 0 until 8) {
            rotate(degrees = i * 45f, pivot = center) {
                drawLine(
                    color = tint,
                    start = center.copy(y = center.y - r),
                    end = center.copy(y = center.y - r * 1.6f),
                    strokeWidth = 2f
                )
            }
        }
    }
}
