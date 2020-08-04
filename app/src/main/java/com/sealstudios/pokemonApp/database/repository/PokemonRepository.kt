package com.sealstudios.pokemonApp.database.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import com.sealstudios.pokemonApp.data.Pokemon
import javax.inject.Inject


class PokemonRepository @Inject constructor(private val pokemonDao: PokemonDao) {

    val allPokemon: LiveData<List<Pokemon>> = pokemonDao.getAllPokemon()

    fun searchPokemon(search: String): LiveData<List<Pokemon>> {
        return pokemonDao.searchAllPokemon(search)
    }

    fun getSinglePokemonById(id: Int): LiveData<Pokemon> {
        return pokemonDao.getSinglePokemonById(id)
    }

    suspend fun insertPokemon(pokemon: Pokemon) {
        pokemonDao.insertPokemon(pokemon)
    }
    
    

    fun updatePokemon(user: Pokemon) {
//        updateUser(pokemonDao, user).execute()
        TODO("Not yet implemented")
    }

    fun deletePokemon(user: Pokemon) {
//        deleteUser(pokemonDao, user).execute()
        TODO("Not yet implemented")
    }

}