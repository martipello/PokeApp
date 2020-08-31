package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonMovesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonMoveJoinDao
import javax.inject.Inject


class PokemonMoveJoinRepository @Inject constructor(
    private val pokemonMoveJoinDao: PokemonMoveJoinDao
) {

    suspend fun insertPokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.insertPokemonMoveJoin(pokemonMovesJoin)
    }

    suspend fun updatePokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.updatePokemonMoveJoin(pokemonMovesJoin)
    }

    suspend fun deletePokemonMovesJoin(pokemonMovesJoin: PokemonMovesJoin) {
        pokemonMoveJoinDao.deletePokemonMoveJoin(pokemonMovesJoin)
    }

}