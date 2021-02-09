package com.sealstudios.pokemonApp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import javax.inject.Inject
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon


class PokemonRepository @Inject constructor(
    private val pokemonDao: PokemonDao
) {

    suspend fun insertPokemon(pokemon: dbPokemon) {
        pokemonDao.insertPokemon(pokemon)
    }

    fun searchPokemonWithTypesAndSpecies(search: String): LiveData<List<PokemonWithTypesAndSpeciesForList>?> {
        return pokemonDao.searchPokemonWithTypesAndSpecies(search)
    }

    fun searchAndFilterPokemonWithTypesAndSpecies(
        search: String,
        filters: List<String>
    ): LiveData<List<PokemonWithTypesAndSpeciesForList>?> {
        return pokemonDao.searchAndFilterPokemonWithTypesAndSpecies(search, filters)
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