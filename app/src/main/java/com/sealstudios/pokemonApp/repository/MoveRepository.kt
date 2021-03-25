package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.Move
import com.sealstudios.pokemonApp.database.`object`.joins.MovesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithMovesAndMetaData
import com.sealstudios.pokemonApp.database.dao.MoveDao
import com.sealstudios.pokemonApp.database.dao.MoveJoinDao
import javax.inject.Inject


class MoveRepository @Inject constructor(
        private val moveDao: MoveDao,
        private val moveJoinDao: MoveJoinDao
) {

    suspend fun insertPokemonMove(move: Move) {
        moveDao.insertPokemonMove(move)
    }

    suspend fun getPokemonMovesAndMetaDataByIdAsync(id: Int): PokemonWithMovesAndMetaData {
        return moveDao.getPokemonWithMovesAndMetaDataByIdAsync(id)
    }

    // MOVE JOINS

    suspend fun insertPokemonMovesJoin(movesJoin: MovesJoin) {
        moveJoinDao.insertPokemonMoveJoin(movesJoin)
    }

}