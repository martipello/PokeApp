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

    suspend fun insertMoveMetaDatas(pokemonMoveMetaData: List<PokemonMoveMetaData>) {
        pokemonMoveMetaDataDao.insertMoveMetaDatas(pokemonMoveMetaData)
    }

    suspend fun movesForIdsAsync(ids: List<Int>): List<PokemonMoveMetaData> {
        return pokemonMoveMetaDataDao.getMoveMetaDataByIdsAsync(ids)
    }

    suspend fun updateMoveMetaData(pokemonMoveMetaData: PokemonMoveMetaData) {
        pokemonMoveMetaDataDao.updateMoveMetaData(pokemonMoveMetaData)
    }

    suspend fun deleteMoveMetaData(pokemonMoveMetaData: PokemonMoveMetaData) {
        pokemonMoveMetaDataDao.deleteMoveMetaData(pokemonMoveMetaData)
    }

    // MOVE JOINS

    suspend fun insertMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin) {
        pokemonMoveMetaDataJoinDao.insertMoveMetaDataJoin(pokemonMoveMetaDataJoin)
    }

    suspend fun insertMoveMetaDataJoins(pokemonMoveMetaDataJoin: List<PokemonMoveMetaDataJoin>) {
        pokemonMoveMetaDataJoinDao.insertMoveMetaDataJoins(pokemonMoveMetaDataJoin)
    }

    suspend fun updateMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin) {
        pokemonMoveMetaDataJoinDao.updateMoveMetaDataJoin(pokemonMoveMetaDataJoin)
    }

    suspend fun deleteMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin) {
        pokemonMoveMetaDataJoinDao.deleteMoveMetaDataJoin(pokemonMoveMetaDataJoin)
    }

}