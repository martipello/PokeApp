package com.sealstudios.pokemonApp.database.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.api.PokemonService
import com.sealstudios.pokemonApp.api.objects.PokemonResponse
import com.sealstudios.pokemonApp.data.Pokemon
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import javax.inject.Inject


class PokemonRepository @Inject constructor(private val pokemonDao: PokemonDao, private val pokemonService: PokemonService) {

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

    suspend fun getRemotePokemon() = pokemonService.getPokemon()

    fun updatePokemon(user: Pokemon) {
//        updateUser(pokemonDao, user).execute()
        TODO("Not yet implemented")
    }

    fun deletePokemon(user: Pokemon) {
//        deleteUser(pokemonDao, user).execute()
        TODO("Not yet implemented")
    }

}