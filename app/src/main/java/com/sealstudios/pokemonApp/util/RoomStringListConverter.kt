package com.sealstudios.pokemonApp.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomStringListConverter {
    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(list, type)
    }
}