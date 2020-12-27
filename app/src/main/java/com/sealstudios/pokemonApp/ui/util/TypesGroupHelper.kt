package com.sealstudios.pokemonApp.ui.util

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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
                chipIcon = ContextCompat.getDrawable(
                    context,
                    pokemonType.icon
                )
                setChipBackgroundColorResource(pokemonType.color)
                isCheckable = false
                isClickable = false
                rippleColor = null
                visibility = View.VISIBLE
            }
        }
    }
}

