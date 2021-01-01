package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon


class PokemonRepository @Inject constructor(
    private val pokemonDao: PokemonDao
) {

//    val allPokemon: LiveData<List<dbPokemon>> = pokemonDao.getAllPokemon()

    fun searchPokemon(search: String): LiveData<List<PokemonWithTypes>> {
        return pokemonDao.getPokemonWithTypes(search)
    }

    fun searchPokemonWithTypesAndSpecies(search: String): LiveData<List<PokemonWithTypesAndSpecies>> {
        return pokemonDao.searchAllPokemonWithTypesAndSpecies(search)
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

    suspend fun updatePokemon(pokemon: dbPokemon) {
        pokemonDao.updatePokemon(pokemon)
    }

    suspend fun deletePokemon(pokemon: dbPokemon) {
        pokemonDao.deletePokemon(pokemon)
    }

    fun getAllPokemonWithTypesAndSpeciesForPaging(): PagingSource<Int, PokemonWithTypesAndSpecies> {
        return pokemonDao.getAllPokemonWithTypesAndSpeciesForPaging()
    }

    fun searchPokemonWithTypesAndSpeciesForPaging(search: String): PagingSource<Int, PokemonWithTypesAndSpeciesForList> {
        return pokemonDao.searchPokemonWithTypesAndSpeciesForPaging(search)
    }

    fun searchAndFilterPokemonWithTypesAndSpeciesForPaging(
        search: String,
        filters: List<String>
    ): PagingSource<Int, PokemonWithTypesAndSpeciesForList> {
        return pokemonDao.searchAndFilterPokemonWithTypesAndSpeciesForPaging(search, filters)
    }

}