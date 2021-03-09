package com.sealstudios.pokemonApp.ui.preferences

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.ui.util.ThemeHelper.Companion.switchUIMode
import com.sealstudios.pokemonApp.ui.util.ThemeHelper.Companion.uiModeKey
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin

class Preferences : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnApplyWindowInsetMargin { myView, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            marginLayoutParams.leftMargin = windowInsets.systemWindowInsetLeft
            marginLayoutParams.rightMargin = windowInsets.systemWindowInsetRight
            marginLayoutParams.bottomMargin = windowInsets.systemWindowInsetBottom
            myView.layoutParams = marginLayoutParams
        }

        val darkModePreference = preferenceManager.findPreference(uiModeKey) as SwitchPreference?
        darkModePreference?.onPreferenceChangeListener = this
        val shareButton = preferenceManager.findPreference("share") as Preference?
        val sendButton = preferenceManager.findPreference("send") as Preference?
        val aboutButton = preferenceManager.findPreference("about") as Preference?
        shareButton?.onPreferenceClickListener = this
        sendButton?.onPreferenceClickListener = this
        aboutButton?.onPreferenceClickListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            uiModeKey -> {
                switchUIMode(preferenceManager.context)
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "about" -> {
            }
            "send" -> {
            }
            "share" -> {
            }
        }
        return true
    }

}