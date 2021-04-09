package com.sealstudios.pokemonApp.database.`object`

import android.annotation.SuppressLint

enum class Gender(val id: Int) {
    FEMALE(id = 1),
    MALE(id = 2),
    GENDERLESS(id = 3);

    companion object {

        @SuppressLint("DefaultLocale")
        fun getGender(genderId: Int): Gender {
            return when(genderId){
                1 -> FEMALE
                2 -> MALE
                else -> GENDERLESS
            }
        }

    }
}