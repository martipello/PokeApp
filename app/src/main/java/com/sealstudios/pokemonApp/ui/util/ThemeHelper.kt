package com.sealstudios.pokemonApp.ui.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class ThemeHelper {
    companion object {

        private fun isNightMode(context: Context) =
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES ->
                        true
                    else -> false
                }

        fun setUiMode(context: Context) {
            val darkMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(uiModeKey, false)
            AppCompatDelegate.setDefaultNightMode(if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

        fun switchUIMode(context: Context) {
            val darkMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(uiModeKey, false)
            AppCompatDelegate.setDefaultNightMode(if (!darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(uiModeKey, !darkMode)
            editor.apply()
        }

        const val uiModeKey = "dark_theme"

    }
}