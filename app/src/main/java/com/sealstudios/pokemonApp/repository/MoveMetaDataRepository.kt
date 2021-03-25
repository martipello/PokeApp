package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.MoveMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.MoveMetaDataJoin
import com.sealstudios.pokemonApp.database.dao.MoveMetaDataDao
import com.sealstudios.pokemonApp.database.dao.MoveMetaDataJoinDao
import javax.inject.Inject


class MoveMetaDataRepository @Inject constructor(
        private val moveMetaDataDao: MoveMetaDataDao,
        private val moveMetaDataJoinDao: MoveMetaDataJoinDao
) {

    suspend fun insertMoveMetaData(moveMetaData: MoveMetaData) {
        moveMetaDataDao.insertMoveMetaData(moveMetaData)
    }

    // MOVE JOINS

    suspend fun insertMoveMetaDataJoin(moveMetaDataJoin: MoveMetaDataJoin) {
        moveMetaDataJoinDao.insertMoveMetaDataJoin(moveMetaDataJoin)
    }

}