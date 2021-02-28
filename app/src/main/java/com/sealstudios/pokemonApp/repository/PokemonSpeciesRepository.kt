package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonSpeciesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesDao
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonSpeciesRepository @Inject constructor(
    private val pokemonSpeciesDao: PokemonSpeciesDao,
    private val pokemonSpeciesJoinDao: PokemonSpeciesJoinDao
) {

    private suspend fun insertPokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.insertSpecies(pokemonSpecies)
    }

    suspend fun getSinglePokemonSpeciesByIdAsync(id: Int): PokemonSpecies? {
        return withContext(Dispatchers.IO) {
            return@withContext pokemonSpeciesDao.getSinglePokemonWithSpeciesByIdAsync(id)
        }
    }

    // SPECIES JOIN

    private suspend fun insertPokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin) {
        pokemonSpeciesJoinDao.insertPokemonSpeciesJoin(pokemonSpeciesJoin)
    }

    suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        withContext(Dispatchers.IO) {
            insertPokemonSpecies(pokemonSpecies)
            insertPokemonSpeciesJoin(
                    PokemonSpeciesJoin(
                            remotePokemonId,
                            pokemonSpecies.id
                    )
            )
        }
    }

}