package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonTypeMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypeMetaDataJoin
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeMetaDataDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeMetaDataJoinDao
import javax.inject.Inject


class PokemonTypeMetaDataRepository @Inject constructor(
        private val pokemonTypeMetaDataDao: PokemonTypeMetaDataDao,
        private val pokemonTypeMetaDataJoinDao: PokemonTypeMetaDataJoinDao
) {

    suspend fun insertPokemonTypeMetaData(pokemonTypeMetaData: PokemonTypeMetaData) {
        pokemonTypeMetaDataDao.insertPokemonTypeMetaData(pokemonTypeMetaData)
    }

    // TYPE META DATA JOIN

    suspend fun insertPokemonTypeMetaDataJoin(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin) {
        pokemonTypeMetaDataJoinDao.insertPokemonTypeMetaData(pokemonTypeMetaDataJoin)
    }

}