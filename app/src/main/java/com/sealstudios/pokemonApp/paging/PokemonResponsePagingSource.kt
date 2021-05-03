package com.sealstudios.pokemonApp.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sealstudios.pokemonApp.api.`object`.PokemonGraphQlPokemon
import com.sealstudios.pokemonApp.api.service.PokemonGraphQLRetroFitService
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import io.github.wax911.library.model.request.QueryContainerBuilder

class PokemonResponsePagingSource(
    private val pokemonService: PokemonGraphQLRetroFitService,
    private val pokemonQuery: QueryContainerBuilder,
    private val pagingConfig: PagingConfig
) : PagingSource<Int, PokemonGraphQL>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonGraphQL> {
        return try {
            val currentLoadingPageKey = params.key ?: 1
            val response = pokemonService.getPokemon(
                query = pokemonQuery.putVariables(
                    mapOf(
                        "limit" to pagingConfig.pageSize,
                        "offset" to pagingConfig.pageSize * currentLoadingPageKey,
                    )
                )
            )
            val data = response.data.map { PokemonGraphQL.mapToPokemonGraphQL(it) }
            val previousKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1
            LoadResult.Page(
                data = data,
                prevKey = previousKey,
                nextKey = if (response.data.size < 20) null else currentLoadingPageKey + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonGraphQL>): Int? {
        return state.anchorPosition
    }

}