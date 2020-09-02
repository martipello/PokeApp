package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.dao.PokemonMoveDao
import javax.inject.Inject


class PokemonMoveRepository @Inject constructor(
    private val pokemonMoveDao: PokemonMoveDao
) {

    suspend fun insertPokemonMove(pokemonMove: PokemonMove) {
        pokemonMoveDao.insertPokemonMove(pokemonMove)
    }

    suspend fun insertPokemonMove(pokemonMove: List<PokemonMove>) {
        pokemonMoveDao.insertPokemonMoves(pokemonMove)
    }

    suspend fun updatePokemonMove(pokemonMove: PokemonMove) {
        pokemonMoveDao.updatePokemonMove(pokemonMove)
    }

    suspend fun deletePokemonMove(pokemonMove: PokemonMove) {
        pokemonMoveDao.deletePokemonMove(pokemonMove)
    }

}