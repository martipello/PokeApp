package com.sealstudios.pokemonApp.api

import com.sealstudios.pokemonApp.api.`object`.PokemonResponse
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PokemonService {

    @GET("pokemon/")
    suspend fun getPokemon(@Query(value = "offset") offset: Int, @Query(value = "limit") limit: Int): Response<PokemonResponse>

    @GET("pokemon/{id}/")
    suspend fun getPokemonById(@Path(value = "id" ) pokemonId: Int, @Query(value = "offset") offset: Int, @Query(value = "limit") limit: Int): Response<Pokemon>

    @GET("pokemon/{name}/")
    suspend fun getPokemonByName(@Path(value = "name") pokemonName: String, @Query(value = "offset") offset: Int, @Query(value = "limit") limit: Int): Response<Pokemon>

}