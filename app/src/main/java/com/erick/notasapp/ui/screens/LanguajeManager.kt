package com.erick.notasapp.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language"

    fun setLocale(context: Context, languageCode: String) {
        saveLanguagePreference(context, languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // 🔹 Usa el contexto base para asegurar que afecte toda la app
        context.applicationContext.resources.updateConfiguration(
            config,
            context.applicationContext.resources.displayMetrics
        )
    }

    fun loadLocale(context: Context) {
        val language = getSavedLanguage(context)
        setLocale(context, language)
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "es") ?: "es" // idioma por defecto: español
    }
}
