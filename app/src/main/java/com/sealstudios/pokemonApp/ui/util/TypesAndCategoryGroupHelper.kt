package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveTypeOrCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TypesAndCategoryGroupHelper(
        private val chipGroup: ChipGroup,
        private val pokemonTypes: List<PokemonMoveTypeOrCategory>
) {
    suspend fun bindChips() {
        withContext(Dispatchers.Default) {
            for (x in pokemonTypes.indices) {
                val pokemonType = pokemonTypes[x]
                if (pokemonType.itemType == PokemonType.itemType) {
                    pokemonTypes[x].type?.let {
                        bindType(x, it)
                    }
                } else {
                    pokemonTypes[x].category?.let {
                        bindCategory(x, it)
                    }
                }
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private suspend fun bindType(childPosition: Int, pokemonType: PokemonType) {
        withContext(Dispatchers.Main) {
            chipGroup.getChildAt(childPosition).apply {
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

    @SuppressLint("DefaultLocale")
    private suspend fun bindCategory(childPosition: Int, pokemonCategory: PokemonCategory) {
        withContext(Dispatchers.Main) {
            chipGroup.getChildAt(childPosition).apply {
                this as Chip
                text = pokemonCategory.name.capitalize()
                chipIcon = ContextCompat.getDrawable(
                        context,
                        pokemonCategory.icon
                )
                setChipBackgroundColorResource(pokemonCategory.color)
                isCheckable = false
                isClickable = false
                rippleColor = null
                visibility = View.VISIBLE
            }
        }

    }

}