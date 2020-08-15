package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.api.PokemonService
import com.sealstudios.pokemonApp.api.`object`.PokemonListResponse
import com.sealstudios.pokemonApp.api.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import retrofit2.Response
import javax.inject.Inject
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon


class PokemonRepository @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val pokemonService: PokemonService
) {

    val allPokemon: LiveData<List<dbPokemon>> = pokemonDao.getAllPokemon()

    fun searchPokemon(search: String): LiveData<List<dbPokemon>> {
        return pokemonDao.searchAllPokemon(search)
    }

    fun searchPokemonWithTypeFilters(search: String, types: List<String>): LiveData<List<dbPokemon>> {
        return pokemonDao.searchPokemonWithTypeFilters(search)
    }

    fun getSinglePokemonById(id: Int): LiveData<dbPokemon> {
        return pokemonDao.getSinglePokemonById(id)
    }

    suspend fun insertPokemon(pokemon: dbPokemon) {
        pokemonDao.insertPokemon(pokemon)
    }

    suspend fun insertPokemon(pokemon: List<dbPokemon>) {
        pokemonDao.insertPokemon(pokemon)
    }

//    suspend fun getRemotePokemon(): Result<PokemonListResponse> {
//        return pokemonService.getPokemon(offset = 0, limit = 10)
//    }

    suspend fun getRemotePokemon(): Response<PokemonListResponse> {
        return pokemonService.getPokemon(offset = 0, limit = 10)
    }

    suspend fun getRemotePokemonById(id: Int): Response<apiPokemon> {
        return pokemonService.getPokemonById(id, offset = 0, limit = 1)
    }

    suspend fun getRemotePokemonSpeciesForId(id: Int): Response<PokemonSpecies> {
        return pokemonService.getPokemonSpeciesForId(id)
    }

    suspend fun getRemotePokemonByName(name: String): Response<apiPokemon> {
        return pokemonService.getPokemonByName(name, offset = 0, limit = 1)
    }

    suspend fun updatePokemon(pokemon: dbPokemon) {
        pokemonDao.updatePokemon(pokemon)
    }

    suspend fun deletePokemon(pokemon: dbPokemon) {
        pokemonDao.deletePokemon(pokemon)
    }

}