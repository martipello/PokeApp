package com.sealstudios.pokemonApp.ui.util

import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getAllPokemonTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FilterGroupHelper(
        private val chipGroup: ChipGroup
) {

    suspend fun bindChips(clickListener: FilterChipClickListener) {
        withContext(Dispatchers.Default) {
            val pokemonTypes = getAllPokemonTypes()
            if (pokemonTypes.size >= chipGroup.childCount) {
                for (x in 0 until chipGroup.childCount) {
                    val pokemonType = pokemonTypes[x]
                    withContext(Dispatchers.Main) {
                        chipGroup.getChildAt(x).apply {
                            this as Chip
                            this.chipIcon = ContextCompat.getDrawable(chipGroup.context, pokemonType.icon)
                            this.text = pokemonType.name
                            this.isChecked = false
                            this.setChipBackgroundColorResource(pokemonType.color)
                            this.setOnClickListener {
                                it as Chip
                                clickListener.onFilterSelected(pokemonType.name, it.isChecked)
                            }
                            this.isChipIconVisible = true
                        }
                    }

                }
            }
        }
    }

    suspend fun setSelections(selections: Set<String>) {
        withContext(Dispatchers.Default) {
            val pokemonTypes = getAllPokemonTypes()
            if (pokemonTypes.size >= chipGroup.childCount) {
                for (x in 0 until chipGroup.childCount) {
                    val pokemonType = pokemonTypes[x]
                    withContext(Dispatchers.Main) {
                        chipGroup.getChildAt(x).apply {
                            this as Chip
                            this.isChecked = selections.contains(pokemonType.name)
                            this.isChipIconVisible = !selections.contains(pokemonType.name)
                        }
                    }

                }
            }
        }
    }
}

