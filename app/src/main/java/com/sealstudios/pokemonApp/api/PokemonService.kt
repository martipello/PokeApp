package com.sealstudios.pokemonApp.api

import com.sealstudios.pokemonApp.api.`object`.PokemonResponse
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface PokemonService {

    @GET("pokemon/")
    suspend fun getPokemon(): Response<PokemonResponse>

    @GET("pokemon/{id}/")
    suspend fun getPokemonById(@Path(value = "id") pokemonId: Int): Response<Pokemon>

    @GET("pokemon/{name}/")
    suspend fun getPokemonByName(@Path(value = "name") pokemonName: String): Response<Pokemon>

}