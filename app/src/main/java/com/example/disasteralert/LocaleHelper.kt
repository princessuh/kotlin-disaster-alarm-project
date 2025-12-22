package com.example.disasteralert

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale
import androidx.core.content.edit

object LocaleHelper {

    private const val PREFS_NAME = "Settings"
    private const val KEY_LANGUAGE = "app_language"

    const val LANGUAGE_KOREAN = "ko"
    const val LANGUAGE_ENGLISH = "en"

    fun setLocale(context: Context, language: String): Context {
        saveLanguage(context, language)
        return updateResources(context, language)
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_KOREAN) ?: LANGUAGE_KOREAN
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, language) }
    }

    fun onAttach(context: Context): Context {
        val language = getLanguage(context)
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun getLanguageDisplayName(language: String): String {
        return when (language) {
            LANGUAGE_KOREAN -> "한국어"
            LANGUAGE_ENGLISH -> "English"
            else -> "한국어"
        }
    }

    fun getLanguageIndex(language: String): Int {
        return when (language) {
            LANGUAGE_KOREAN -> 0
            LANGUAGE_ENGLISH -> 1
            else -> 0
        }
    }

    fun getLanguageFromIndex(index: Int): String {
        return when (index) {
            0 -> LANGUAGE_KOREAN
            1 -> LANGUAGE_ENGLISH
            else -> LANGUAGE_KOREAN
        }
    }
}
