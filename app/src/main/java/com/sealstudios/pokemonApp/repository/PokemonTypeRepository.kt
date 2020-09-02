package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import javax.inject.Inject


class PokemonTypeRepository @Inject constructor(
    private val pokemonTypeDao: PokemonTypeDao
) {

    suspend fun insertPokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.insertPokemonType(pokemonType)
    }

    suspend fun updatePokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.updatePokemonType(pokemonType)
    }

    suspend fun deletePokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.deletePokemonType(pokemonType)
    }

}