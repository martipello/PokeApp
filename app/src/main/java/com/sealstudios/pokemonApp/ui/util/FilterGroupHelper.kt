package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getAllPokemonTypes


class FilterGroupHelper(private val chipGroup: ChipGroup, private val clickListener: FilterChipClickListener) {

    fun bindChips() {
        //TODO capture touches here disallow for below view
        val pokemonTypes = getAllPokemonTypes()
        if (pokemonTypes.size <= chipGroup.childCount) {
            for (x in 0 until chipGroup.childCount) {
                val pokemonType = pokemonTypes[x]
                chipGroup.getChildAt(x).apply {
                    val chip = this as Chip
                    this.chipIcon = ContextCompat.getDrawable(chipGroup.context, pokemonType.icon)
                    this.text = pokemonType.name
                    this.setChipBackgroundColorResource(pokemonType.color)
                    this.setOnClickListener {
                        it as Chip
                        it.isChipIconVisible = !it.isChipIconVisible
                        it.isChecked = it.isChecked
                        clickListener.onFilterSelected(pokemonType.name, it.isChecked)
                    }
                }
            }
        }
    }
}