package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonSpeciesJoin
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesJoinDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import javax.inject.Inject


class PokemonSpeciesJoinRepository @Inject constructor(
    private val pokemonSpeciesJoinDao: PokemonSpeciesJoinDao
) {

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