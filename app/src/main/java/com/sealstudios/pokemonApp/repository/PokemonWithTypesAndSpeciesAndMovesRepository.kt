package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import javax.inject.Inject


class PokemonWithTypesAndSpeciesAndMovesRepository @Inject constructor(
    private val pokemonDao: PokemonDao
) {

    fun getSinglePokemonById(id: Int): LiveData<PokemonWithTypesAndSpeciesAndMoves> {
        return pokemonDao.getSinglePokemonWithTypesAndSpeciesAndMovesById(id)
    }
}