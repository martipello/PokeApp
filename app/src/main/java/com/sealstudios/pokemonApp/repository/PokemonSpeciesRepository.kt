package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonSpeciesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesDao
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonSpeciesRepository @Inject constructor(
    private val pokemonSpeciesDao: PokemonSpeciesDao,
    private val pokemonSpeciesJoinDao: PokemonSpeciesJoinDao
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

    // SPECIES JOIN

    suspend fun insertPokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin) {
        pokemonSpeciesJoinDao.insertPokemonSpeciesJoin(pokemonSpeciesJoin)
    }

    suspend fun updatePokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin) {
        pokemonSpeciesJoinDao.updatePokemonSpeciesJoin(pokemonSpeciesJoin)
    }

    suspend fun deletePokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin) {
        pokemonSpeciesJoinDao.deletePokemonSpeciesJoin(pokemonSpeciesJoin)
    }

}