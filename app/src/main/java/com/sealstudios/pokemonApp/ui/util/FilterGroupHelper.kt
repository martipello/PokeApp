package com.sealstudios.pokemonApp.ui.util

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getAllPokemonTypes


class FilterGroupHelper(
    private val chipGroup: ChipGroup,
    private val clickListener: FilterChipClickListener,
    private val selections: Map<String, Boolean>
) {

    fun bindChips() {
        val pokemonTypes = getAllPokemonTypes()
        if (pokemonTypes.size <= chipGroup.childCount) {
            for (x in 0 until chipGroup.childCount) {
                val pokemonType = pokemonTypes[x]
                chipGroup.getChildAt(x).apply {
                    this as Chip
                    this.chipIcon = ContextCompat.getDrawable(chipGroup.context, pokemonType.icon)
                    this.text = pokemonType.name
                    this.isChipIconVisible = !selections.getOrElse(pokemonType.name) { false }
                    this.isChecked = selections[pokemonType.name] ?: false
                    this.setChipBackgroundColorResource(pokemonType.color)
                    this.setOnClickListener {
                        it as Chip
                        clickListener.onFilterSelected(pokemonType.name, it.isChecked)
                    }
                }
            }
        }
    }
}

class TypesGroupHelper(
    private val chipGroup: ChipGroup,
    private val pokemonTypes: List<PokemonType>
) {
    fun bindChips() {
        for (x in pokemonTypes.indices) {
            val pokemonType = pokemonTypes[x]
            chipGroup.getChildAt(x).apply {
                this as Chip
                text = pokemonType.name.capitalize()
                chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
                setChipBackgroundColorResource(pokemonType.color)
                isCheckable = false
                isClickable = false
                rippleColor = null
                visibility = View.VISIBLE
            }
        }
    }
}