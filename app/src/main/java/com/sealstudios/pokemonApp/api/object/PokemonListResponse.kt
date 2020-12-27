package com.sealstudios.pokemonApp.api.`object`

data class PokemonListResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<NamedApiResource>
)