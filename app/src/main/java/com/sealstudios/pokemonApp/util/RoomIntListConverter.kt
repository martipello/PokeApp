package com.sealstudios.pokemonApp.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomIntListConverter {
    @TypeConverter
    fun fromStringToList(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromListToString(list: List<Int>): String {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().toJson(list, type)
    }
}