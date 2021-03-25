package com.sealstudios.pokemonApp.ui.adapter.helperObjects

import com.sealstudios.pokemonApp.database.`object`.wrappers.MoveWithMetaData

data class MoveAdapterItem(
        val moveWithMetaData: MoveWithMetaData?,
        val header: GenerationHeader?,
        val itemType: Int
)
