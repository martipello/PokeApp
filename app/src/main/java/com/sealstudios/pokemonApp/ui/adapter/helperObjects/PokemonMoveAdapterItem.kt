package com.sealstudios.pokemonApp.ui.adapter.helperObjects

import com.sealstudios.pokemonApp.database.`object`.PokemonMove

data class PokemonMoveAdapterItem(
    val move: PokemonMove?,
    val header: GenerationHeader?,
    val itemType: Int
)
