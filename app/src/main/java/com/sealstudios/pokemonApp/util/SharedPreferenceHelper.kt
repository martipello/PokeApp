package com.sealstudios.pokemonApp.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(prefsFileName, 0)

    fun getBool(key: String): Boolean {
        return sharedPreferences.getBoolean(key, true)
    }

    fun setBool(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

//    fun getString(key: String): String? {
//        return sharedPreferences.getString(key, null)
//    }
//
//    fun setString(key: String, value: String) {
//        val editor = sharedPreferences.edit()
//        editor.putString(key, value)
//        editor.apply()
//    }

    companion object {
        const val prefsFileName = "com.sealstudios.pokemonApp.prefs"
        const val isFirstTime = "pokemonApp.isFirstTime"
    }

}