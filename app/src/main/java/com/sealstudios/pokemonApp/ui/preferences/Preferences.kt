package com.sealstudios.pokemonApp.ui.preferences

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.ui.util.ThemeHelper.Companion.switchUIMode
import com.sealstudios.pokemonApp.ui.util.ThemeHelper.Companion.uiModeKey
import com.sealstudios.pokemonApp.util.extensions.toLowerCase

class Preferences : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                findNavController().navigate(R.id.action_preferences_to_aboutFragment)
            }
            "send" -> {
                sendEmail()
            }
            "share" -> {
                shareApp()
            }
        }
        return true
    }

    private fun shareApp() {
        val appNameId = preferenceManager.context.applicationInfo.labelRes
        val packageName = preferenceManager.context.packageName
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(appNameId))
        val shareTitle = getString(R.string.share_title)
        val shareLink = getString(R.string.play_store_link, packageName)
        sendIntent.putExtra(Intent.EXTRA_TEXT, "$shareTitle $shareLink")
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_via)))
    }

    private fun sendEmail() {
        val email = Intent(Intent.ACTION_SEND)
        email.type = "text/email"
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback))
        email.putExtra(Intent.EXTRA_TEXT,
                """
                Please delete as appropriate...
                
                I'm contacting you to 
                
                Give Feedback : 
                File a Bug Report : 
                Request a Feature : 
                
                
                Please do not delete this...
                
                Device : ${getDeviceName()}
                Version : ${Build.VERSION.CODENAME}
                API : ${Build.VERSION.SDK_INT}
                """.trimIndent())
        startActivity(Intent.createChooser(email, "Contact us"))
    }

    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            model
        } else {
            "$manufacturer $model"
        }
    }

}