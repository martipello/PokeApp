package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaDataJoin
import com.sealstudios.pokemonApp.database.dao.PokemonMoveMetaDataDao
import com.sealstudios.pokemonApp.database.dao.PokemonMoveMetaDataJoinDao
import javax.inject.Inject


class PokemonMoveMetaDataRepository @Inject constructor(
    private val pokemonMoveMetaDataDao: PokemonMoveMetaDataDao,
    private val pokemonMoveMetaDataJoinDao: PokemonMoveMetaDataJoinDao
) {

    suspend fun insertMoveMetaData(pokemonMoveMetaData: PokemonMoveMetaData) {
        pokemonMoveMetaDataDao.insertMoveMetaData(pokemonMoveMetaData)
    }

    // MOVE JOINS

    suspend fun insertMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin) {
        pokemonMoveMetaDataJoinDao.insertMoveMetaDataJoin(pokemonMoveMetaDataJoin)
    }

}