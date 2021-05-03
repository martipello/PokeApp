package com.sealstudios.pokemonApp.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sealstudios.pokemonApp.api.`object`.PokemonGraphQlPokemon
import com.sealstudios.pokemonApp.api.service.PokemonGraphQLRetroFitService
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import com.sealstudios.pokemonApp.database.`object`.PokemonRemoteKey
import io.github.wax911.library.model.request.QueryContainerBuilder
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PokemonResponseRemoteMediator @Inject constructor(
    private val pokemonService: PokemonGraphQLRetroFitService,
    private val pokemonQuery: QueryContainerBuilder,
    private val pokemonRoomDatabase: PokemonRoomDatabase,
    private val pokemonRepository: PokemonGraphQLRepository,
    private val remoteKeyRepository: RemoteKeyRepository
) : RemoteMediator<Int, PokemonGraphQL>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonGraphQL>
    ): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val query = pokemonQuery.putVariables(
                mapOf(
                    "limit" to state.config.pageSize,
                    "offset" to state.config.pageSize * page,
                )
            )
            val response = pokemonService.getPokemon(query)
            val isEndOfList = response.data.isEmpty()
            pokemonRoomDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    remoteKeyRepository.clearTable()
                    pokemonRepository.clearTable()
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.data.map {
                    PokemonRemoteKey(repoId = it.id, prevPageKey = prevKey, nextPageKey = nextKey)
                }
                remoteKeyRepository.insertKey(keys)
                pokemonRepository.insertPokemonGraphQL(response.data.map { PokemonGraphQL.mapToPokemonGraphQL(it)})
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, PokemonGraphQL>): PokemonRemoteKey? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { pokemon -> remoteKeyRepository.remoteKey(pokemon.id) }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, PokemonGraphQL>): PokemonRemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { pokemon -> remoteKeyRepository.remoteKey(pokemon.id) }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, PokemonGraphQL>): PokemonRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                remoteKeyRepository.remoteKey(repoId)
            }
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, PokemonGraphQL>
    ): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextPageKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextPageKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                    ?: throw InvalidObjectException("Invalid state, key should not be null")
                //end of list condition reached
                remoteKeys.prevPageKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevPageKey
            }
        }
    }

    companion object {
        const val DEFAULT_PAGE_INDEX = 0
    }
}