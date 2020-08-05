package com.sealstudios.pokemonApp.database.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.api.PokemonService
import com.sealstudios.pokemonApp.api.`object`.PokemonResponse
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import retrofit2.Response
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

    suspend fun insertPokemon(pokemon: List<Pokemon>) {
        pokemonDao.insertPokemon(pokemon)
    }

    suspend fun getRemotePokemon(): Response<PokemonResponse> {
        return pokemonService.getPokemon(offset = 0, limit = 1000)
    }

    suspend fun getRemotePokemonById(id: Int): Response<Pokemon> {
        return pokemonService.getPokemonById(id, offset = 0, limit = 1)
    }

    suspend fun getRemotePokemonByName(name: String): Response<Pokemon> {
        return pokemonService.getPokemonByName(name, offset = 0, limit = 1)
    }

    suspend fun updatePokemon(pokemon: Pokemon) {
        pokemonDao.updatePokemon(pokemon)
    }

    suspend fun deletePokemon(pokemon: Pokemon) {
        pokemonDao.deletePokemon(pokemon)
    }

}