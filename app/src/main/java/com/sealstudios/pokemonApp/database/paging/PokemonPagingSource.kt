package com.sealstudios.pokemonApp.database.paging
//
//import android.util.Log
//import androidx.paging.PagingSource
//import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
//import com.sealstudios.pokemonApp.repository.PokemonRepository
//
//private const val STARTING_PAGE_INDEX = 1
//
//class PokemonPagingSource(
//    private val pokemonRepository : PokemonRepository,
//    private val search : String
//) :  PagingSource<Int, PokemonWithTypesAndSpecies>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonWithTypesAndSpecies> {
//        val position = params.key ?: STARTING_PAGE_INDEX
//
//        val pokemon = pokemonRepository.searchPokemonWithTypesAndSpeciesForPaging(search)
//        Log.d("PokemonPagingSource",  "$pokemon")
//        return LoadResult.Page(
//            data = pokemon,
//            prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
//            nextKey = if (pokemon.isEmpty()) null else position + 1
//        )
//    }
//
//}