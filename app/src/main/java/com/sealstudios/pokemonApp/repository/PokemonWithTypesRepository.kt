package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import javax.inject.Inject


class PokemonWithTypesRepository @Inject constructor(
    private val pokemonDao: PokemonDao
) {

    fun getSinglePokemonById(id: Int): LiveData<PokemonWithTypes> {
        return pokemonDao.getSinglePokemonWithTypesById(id)
    }
}