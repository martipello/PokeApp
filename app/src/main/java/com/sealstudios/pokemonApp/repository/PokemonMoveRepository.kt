package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.dao.PokemonMoveDao
import javax.inject.Inject


class PokemonMoveRepository @Inject constructor(
    private val pokemonMoveDao: PokemonMoveDao
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

    fun movesForIds(ids: List<Int>): LiveData<List<PokemonMove>> {
        return pokemonMoveDao.getMovesByIds(ids)
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

}