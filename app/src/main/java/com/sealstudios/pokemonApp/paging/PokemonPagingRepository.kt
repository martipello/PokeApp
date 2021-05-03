package com.sealstudios.pokemonApp.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sealstudios.pokemonApp.api.`object`.PokemonGraphQlPokemon
import com.sealstudios.pokemonApp.api.service.PokemonGraphQLRetroFitService
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import io.github.wax911.library.model.request.QueryContainerBuilder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PokemonPagingRepository @Inject constructor(
    private val pokemonResponseRemoteMediator: PokemonResponseRemoteMediator,
    private val pokemonGraphQLRetroFitService: PokemonGraphQLRetroFitService
) {
    @ExperimentalPagingApi
    fun getPokemonGraphQL(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
        queryContainerBuilder: QueryContainerBuilder
    ): Flow<PagingData<PokemonGraphQL>> {
        val pagingSourceFactory = {
            PokemonResponsePagingSource(
                pokemonGraphQLRetroFitService,
                queryContainerBuilder,
                pagingConfig
            )
        }
        return Pager(
            config = pagingConfig,
            null,
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = pokemonResponseRemoteMediator
        ).flow
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 20

    }

}