package com.sealstudios.pokemonApp.ui.adapter.helperObjects

import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonMoveWithMetaData

data class PokemonMoveAdapterItem(
        val moveWithMetaData: PokemonMoveWithMetaData?,
        val header: GenerationHeader?,
        val itemType: Int
)
