package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun updatePokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.updatePokemonType(pokemonType)
    }

    suspend fun deletePokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.deletePokemonType(pokemonType)
    }

    //TYPE JOIN

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