package com.erick.notasapp.ui.utils

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val activity = LocalContext.current as ComponentActivity
    return calculateWindowSizeClass(activity)
}

