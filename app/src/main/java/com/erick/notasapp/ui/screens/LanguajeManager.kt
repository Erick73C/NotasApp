package com.erick.notasapp.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

object LanguageManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language"

    fun setLocale(context: Context, languageCode: String) {
        saveLanguagePreference(context, languageCode)
        applyLocale(context, languageCode)
    }

    private fun applyLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
        } else {
            configuration.locale = locale
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
    }

    fun loadLocale(context: Context) {
        val language = getSavedLanguage(context)
        applyLocale(context, language)
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "es") ?: "es"
    }
}