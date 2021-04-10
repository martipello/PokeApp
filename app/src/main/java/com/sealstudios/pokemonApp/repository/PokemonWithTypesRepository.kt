package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes
import com.sealstudios.pokemonApp.database.dao.TypeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonWithTypesRepository @Inject constructor(
    private val typeDao: TypeDao
) {

    suspend fun getSinglePokemonWithTypesByIdAsync(id: Int): PokemonWithTypes {
        return withContext(Dispatchers.IO) {
            return@withContext typeDao.getPokemonWithTypesById(id)
        }
    }

}