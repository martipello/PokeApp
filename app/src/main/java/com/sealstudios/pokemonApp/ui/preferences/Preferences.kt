package com.sealstudios.pokemonApp.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sealstudios.pokemonApp.R

class Preferences : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}