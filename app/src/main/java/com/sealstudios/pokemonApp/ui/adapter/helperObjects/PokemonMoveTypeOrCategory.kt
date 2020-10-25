package com.sealstudios.pokemonApp.ui.adapter.helperObjects

import com.sealstudios.pokemonApp.ui.util.PokemonCategory
import com.sealstudios.pokemonApp.ui.util.PokemonType

data class PokemonMoveTypeOrCategory(
    val type: PokemonType?,
    val category: PokemonCategory?,
    val itemType: Int
)
