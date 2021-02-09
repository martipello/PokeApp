package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import javax.inject.Inject


class PokemonTypeRepository @Inject constructor(
    private val pokemonTypeDao: PokemonTypeDao,
    private val pokemonTypeJoinDao: PokemonTypeJoinDao
) {

    suspend fun insertPokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.insertPokemonType(pokemonType)
    }

    suspend fun insertPokemonTypes(pokemonType: List<PokemonType>) {
        pokemonTypeDao.insertPokemonTypes(pokemonType)
    }

    //TYPE JOIN

    suspend fun insertPokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin) {
        pokemonTypeJoinDao.insertPokemonTypeJoin(pokemonTypeJoin)
    }

    suspend fun insertPokemonTypeJoins(pokemonTypeJoin: List<PokemonTypesJoin>) {
        pokemonTypeJoinDao.insertPokemonTypeJoins(pokemonTypeJoin)
    }

}