package com.sealstudios.pokemonApp.api.`object`

import com.sealstudios.pokemonApp.database.`object`.Pokemon

data class PokemonListResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: ArrayList<Pokemon?>?
)