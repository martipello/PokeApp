package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.dao.PokemonDao
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

    fun getSinglePokemonWithTypesById(id: Int): LiveData<PokemonWithTypes> {
        return pokemonTypeDao.getSinglePokemonWithTypesById(id)
    }

    fun getAllPokemonWithTypes(search: String): LiveData<List<PokemonWithTypes>> {
        return pokemonTypeDao.getPokemonWithTypes(search = search)
    }

}