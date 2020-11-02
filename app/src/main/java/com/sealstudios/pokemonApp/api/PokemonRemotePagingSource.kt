package com.sealstudios.pokemonApp.api
//
//import androidx.paging.PagingSource
//import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
//import com.sealstudios.pokemonApp.api.service.PokemonService
//import javax.inject.Inject
//
//private const val STARTING_PAGE = 1
//private const val OFFSET = 80
//
//class PokemonRemotePagingSource @Inject constructor(
//    private val service: PokemonService
//) : PagingSource<Int, NamedApiResource>() {
//
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult.Page<Int, NamedApiResource> {
//        val position = params.key ?: STARTING_PAGE
//        return try {
//            val response = service.getPokemon(  offset = OFFSET + position, limit = OFFSET)
//            val pokemon = response.results
////            next:"https://pokeapi.co/api/v2/pokemon?offset=300&limit=100"
////            previous:"https://pokeapi.co/api/v2/pokemon?offset=100&limit=100"
//            LoadResult.Page(
//                data = pokemon,
//                prevKey = if (position == STARTING_PAGE) null else position + 1,
//                nextKey =  if (pokemon.isEmpty()) null else position + 1
//            )
//        } catch (e: Exception) {
//            // Handle errors in this block
//            return LoadResult.Error(exception)
//        }
//    }
//
//}