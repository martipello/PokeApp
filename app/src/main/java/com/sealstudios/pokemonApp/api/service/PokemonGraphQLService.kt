package com.sealstudios.pokemonApp.api.service

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.sealstudios.PokemonQuery
import com.sealstudios.pokemonApp.api.`object`.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject


class PokemonGraphQLService @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun getPokemon(): Resource<List<PokemonQuery.Pokemon_v2_pokemon>> {
        return withContext(Dispatchers.IO) {
            val response = try {
                apolloClient.query(PokemonQuery()).await()
            } catch (e: ApolloException) {
                return@withContext Resource.error(
                    msg = "",
                    data = emptyList(),
                    code = -1
                )
            }
            val pokemonList = response.data?.pokemon_v2_pokemon
            if (pokemonList == null || response.hasErrors()) {
                // handle application errors
                return@withContext Resource.error(
                    msg = "",
                    data = emptyList(),
                    code = -1
                )
            }
            return@withContext Resource.success(
                data = response.data?.pokemon_v2_pokemon,
            )
        }
    }

}