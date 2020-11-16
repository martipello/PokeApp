package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonType as pokemonType

enum class PokemonCategory(val color: Int, val icon: Int) {
    PHYSICAL(color = R.color.physical, icon = R.drawable.physical),
    SPECIAL(color = R.color.special, icon = R.drawable.special),
    STATUS(color = R.color.status, icon = R.drawable.status),
    UNKNOWN(color = R.color.white, icon = R.drawable.ic_pokeball);

    companion object {

        const val itemType = 1001

        @SuppressLint("DefaultLocale")
        fun getCategoryForDamageClass(type: String): PokemonCategory {
            Log.d("PokemonCategory","getPokemonEnumCategoryForPokemonType $type")
            return try {
                valueOf(type.toUpperCase())
            } catch (e: Exception) {
                UNKNOWN
            }
        }

        @SuppressLint("DefaultLocale")
        fun createPokemonCategoryChip(pokemonType: PokemonCategory, context: Context): Chip? {
            val chip =
                LayoutInflater.from(context).inflate(R.layout.pokemon_type_chip, null) as Chip
            chip.text = pokemonType.name.capitalize()
            chip.chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
            chip.setChipBackgroundColorResource(pokemonType.color)
            chip.isCheckable = false
            chip.isClickable = false
            chip.rippleColor = null
            return chip
        }

        @SuppressLint("DefaultLocale", "InflateParams")
        fun setPokemonCategoryChip(pokemonType: PokemonCategory, context: Context, chip: Chip) {
            chip.text = pokemonType.name.capitalize()
            chip.chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
            chip.setChipBackgroundColorResource(pokemonType.color)
            chip.isCheckable = false
            chip.isClickable = false
            chip.rippleColor = null
            chip.visibility = View.VISIBLE
        }

    }

}


