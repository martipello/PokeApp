package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonSpeciesRepository @Inject constructor(
    private val pokemonSpeciesDao: PokemonSpeciesDao
) {

    suspend fun insertPokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.insertSpecies(pokemonSpecies)
    }

    suspend fun insertPokemonSpecies(pokemonSpecies: List<PokemonSpecies>) {
        pokemonSpeciesDao.insertSpecies(pokemonSpecies)
    }

    suspend fun getSinglePokemonSpeciesByIdAsync(id: Int): PokemonSpecies? {
        return withContext(Dispatchers.IO) {
            return@withContext pokemonSpeciesDao.getSinglePokemonWithSpeciesByIdAsync(id)
        }
    }

    suspend fun updatePokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.updateSpecies(pokemonSpecies)
    }

    suspend fun deletePokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.deleteSpecies(pokemonSpecies)
    }

}