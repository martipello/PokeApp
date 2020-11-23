package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import javax.inject.Inject


class PokemonTypeJoinRepository @Inject constructor(
    private val pokemonTypeJoinDao: PokemonTypeJoinDao
) {

    suspend fun insertPokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin) {
        pokemonTypeJoinDao.insertPokemonTypeJoin(pokemonTypeJoin)
    }

    suspend fun insertPokemonTypeJoins(pokemonTypeJoin: List<PokemonTypesJoin>) {
        pokemonTypeJoinDao.insertPokemonTypeJoins(pokemonTypeJoin)
    }

    suspend fun updatePokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin) {
        pokemonTypeJoinDao.updatePokemonTypeJoin(pokemonTypeJoin)
    }

    suspend fun deletePokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin) {
        pokemonTypeJoinDao.deletePokemonTypeJoin(pokemonTypeJoin)
    }

}