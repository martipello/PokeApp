package com.sealstudios.pokemonApp.api.objects

import com.sealstudios.pokemonApp.data.Pokemon

data class PokemonResponse(
        val count: Int,
        val next: String,
        val previous: String,
        val results: ArrayList<Pokemon>,
)