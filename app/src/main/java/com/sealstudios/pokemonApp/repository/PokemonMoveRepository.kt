package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonWithMovesAndMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonMovesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonMoveDao
import com.sealstudios.pokemonApp.database.dao.PokemonMoveJoinDao
import javax.inject.Inject


class PokemonMoveRepository @Inject constructor(
    private val pokemonMoveDao: PokemonMoveDao,
    private val pokemonMoveJoinDao: PokemonMoveJoinDao
) {

    suspend fun insertPokemonMove(pokemonMove: PokemonMove) {
        pokemonMoveDao.insertPokemonMove(pokemonMove)
    }

    suspend fun insertPokemonMoves(pokemonMove: List<PokemonMove>) {
        pokemonMoveDao.insertPokemonMoves(pokemonMove)
    }

    fun getSingleMoveById(id: Int): LiveData<PokemonMove> {
        return pokemonMoveDao.getMoveById(id)
    }

    fun getPokemonMovesAndMetaDataById(id: Int): LiveData<PokemonWithMovesAndMetaData> {
        return pokemonMoveDao.getPokemonWithMovesAndMetaDataById(id)
    }

    fun movesForIds(ids: List<Int>): LiveData<List<PokemonMove>> {
        return pokemonMoveDao.getMovesByIds(ids)
    }

    suspend fun movesForIdsAsync(ids: List<Int>): List<PokemonMove> {
        return pokemonMoveDao.getMovesByIdsAsync(ids)
    }

    suspend fun getPokemonMovesAndMetaDataByIdAsync(id: Int): PokemonWithMovesAndMetaData {
        return pokemonMoveDao.getPokemonWithMovesAndMetaDataByIdAsync(id)
    }

    suspend fun updatePokemonMove(pokemonMove: PokemonMove) {
        pokemonMoveDao.updatePokemonMove(pokemonMove)
    }

    suspend fun deletePokemonMove(pokemonMove: PokemonMove) {
        pokemonMoveDao.deletePokemonMove(pokemonMove)
    }

    suspend fun moveExists(pokemonMoveId: Int) : Boolean {
        return pokemonMoveDao.moveExists(pokemonMoveId)
    }

    // MOVE JOINS

    suspend fun insertPokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.insertPokemonMoveJoin(pokemonMovesJoin)
    }

    suspend fun insertPokemonMovesJoins(pokemonMovesJoin: List<PokemonMovesJoin>) {
        pokemonMoveJoinDao.insertPokemonMoveJoin(pokemonMovesJoin)
    }

    suspend fun updatePokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.updatePokemonMoveJoin(pokemonMovesJoin)
    }

    suspend fun deletePokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.deletePokemonMoveJoin(pokemonMovesJoin)
    }

}