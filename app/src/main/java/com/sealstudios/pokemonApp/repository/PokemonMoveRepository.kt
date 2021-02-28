package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMovesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithMovesAndMetaData
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

    suspend fun getPokemonMovesAndMetaDataByIdAsync(id: Int): PokemonWithMovesAndMetaData {
        return pokemonMoveDao.getPokemonWithMovesAndMetaDataByIdAsync(id)
    }

    // MOVE JOINS

    suspend fun insertPokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.insertPokemonMoveJoin(pokemonMovesJoin)
    }

}