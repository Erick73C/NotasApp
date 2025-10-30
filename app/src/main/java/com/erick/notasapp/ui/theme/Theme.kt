package com.erick.notasapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import com.erick.notasapp.ui.screens.PinkLight
import com.erick.notasapp.ui.screens.PinkMain
import com.erick.notasapp.ui.screens.WhiteCard

// --- Dark theme colors ---
private val DarkColorScheme = darkColorScheme(
    primary = PinkBright,             // Rosa fuerte para botones y acentos
    secondary = PinkSoft,             // Rosa suave para elementos secundarios
    tertiary = PinkBright,            // Rosa fuerte también en elementos terciarios
    background = PinkBackgroundDark,  // Fondo oscuro principal
    surface = PinkCard,               // Tarjetas oscuras
    onPrimary = Color.White,          // Texto sobre botones rosa
    onBackground = Color.White,       // Texto sobre fondo oscuro → blanco
    onSurface = Color.White,          // Texto sobre tarjetas → blanco
    // Asegurando que todos los textos serán blancos en el tema oscuro
    onSecondary = Color.White,        // Texto sobre elementos secundarios
    onTertiary = Color.White          // Texto sobre elementos terciarios
)

// --- Light theme colors ---
private val LightColorScheme = lightColorScheme(
    primary = PinkMain,
    secondary = PinkLight,
    tertiary = WhiteCard,
    background = PinkBackgroundLight,
    surface = WhiteCard,
    onPrimary = Color.White,
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF1F2937),
    onSecondary = Color(0xFF1F2937),   // Texto en color oscuro para tema claro
    onTertiary = Color(0xFF1F2937)      // Texto en color oscuro para tema claro
)

// --- Theme Composable ---
@Composable
fun NotasAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
