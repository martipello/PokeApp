package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonWithTypesAndSpeciesRepository @Inject constructor(
    private val pokemonDao: PokemonDao
) {

    fun getSinglePokemonById(id: Int): LiveData<PokemonWithTypesAndSpecies> {
        return pokemonDao.getSinglePokemonWithTypesAndSpeciesById(id)
    }

    suspend fun getSinglePokemonWithTypesByIdAsync(id: Int): PokemonWithTypes {
        return withContext(Dispatchers.IO){
            return@withContext pokemonDao.getSinglePokemonWithTypesByIdAsync(id)
        }
    }

    suspend fun insertPokemon(pokemon: Pokemon) {
        pokemonDao.insertPokemon(pokemon)
    }

    suspend fun updatePokemon(pokemon: Pokemon) {
        pokemonDao.updatePokemon(pokemon)
    }
}