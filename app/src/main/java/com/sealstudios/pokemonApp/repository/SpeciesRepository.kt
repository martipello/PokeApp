package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.Species
import com.sealstudios.pokemonApp.database.`object`.joins.SpeciesJoin
import com.sealstudios.pokemonApp.database.dao.SpeciesDao
import com.sealstudios.pokemonApp.database.dao.SpeciesJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SpeciesRepository @Inject constructor(
        private val speciesDao: SpeciesDao,
        private val speciesJoinDao: SpeciesJoinDao
) {

    private suspend fun insertPokemonSpecies(species: Species) {
        speciesDao.insertSpecies(species)
    }

    suspend fun getSinglePokemonSpeciesByIdAsync(id: Int): Species? {
        return withContext(Dispatchers.IO) {
            return@withContext speciesDao.getSinglePokemonWithSpeciesByIdAsync(id)
        }
    }

    // SPECIES JOIN

    private suspend fun insertPokemonSpeciesJoin(speciesJoin: SpeciesJoin) {
        speciesJoinDao.insertPokemonSpeciesJoin(speciesJoin)
    }

    suspend fun insertPokemonSpecies(remotePokemonId: Int, species: Species) {
        withContext(Dispatchers.IO) {
            insertPokemonSpecies(species)
            insertPokemonSpeciesJoin(
                    SpeciesJoin(
                            remotePokemonId,
                            species.id
                    )
            )
        }
    }

}