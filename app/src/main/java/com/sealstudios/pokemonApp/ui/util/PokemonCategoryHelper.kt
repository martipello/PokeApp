package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import com.sealstudios.pokemonApp.R

enum class PokemonCategory(val color: Int, val icon: Int) {
    PHYSICAL(color = R.color.physical, icon = R.drawable.physical),
    SPECIAL(color = R.color.special, icon = R.drawable.special),
    STATUS(color = R.color.status, icon = R.drawable.status),
    UNKNOWN(color = R.color.white, icon = R.drawable.ic_pokeball);

    companion object {

        const val itemType = 1001

        @SuppressLint("DefaultLocale")
        fun getCategoryForDamageClass(type: String): PokemonCategory {
            return try {
                valueOf(type.toUpperCase())
            } catch (e: Exception) {
                UNKNOWN
            }
        }

    }

}


