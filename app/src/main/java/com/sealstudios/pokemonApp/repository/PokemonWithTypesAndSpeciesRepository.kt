package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import javax.inject.Inject


class PokemonWithTypesAndSpeciesRepository @Inject constructor(
    private val pokemonDao: PokemonDao
) {

    suspend fun updatePokemon(pokemon: Pokemon) {
        pokemonDao.updatePokemon(pokemon)
    }
}