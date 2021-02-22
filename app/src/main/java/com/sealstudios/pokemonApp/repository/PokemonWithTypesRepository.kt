package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonWithTypesRepository @Inject constructor(
    private val pokemonTypeDao: PokemonTypeDao
) {

    suspend fun getSinglePokemonWithTypesByIdAsync(id: Int): PokemonWithTypes {
        return withContext(Dispatchers.IO) {
            return@withContext pokemonTypeDao.getSinglePokemonWithTypesByIdAsync(id)
        }
    }

}