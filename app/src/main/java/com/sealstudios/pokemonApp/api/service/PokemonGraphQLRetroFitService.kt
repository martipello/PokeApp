package com.sealstudios.pokemonApp.api.service

import com.sealstudios.PokemonQuery
import com.sealstudios.pokemonApp.api.`object`.*
import io.github.wax911.library.annotation.GraphQuery
import io.github.wax911.library.model.request.QueryContainerBuilder
import retrofit2.Response
import retrofit2.http.*


interface PokemonGraphQLRetroFitService {
    @POST("https://beta.pokeapi.co/graphql/v1beta")
    @GraphQuery("PokemonQuery")
//    @Headers("X-Method-Used graphiql")
    suspend fun getPokemon(
        @Body query: QueryContainerBuilder,
    ): PokemonGraphQlResponse

}