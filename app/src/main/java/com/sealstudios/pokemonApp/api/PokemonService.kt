package com.sealstudios.pokemonApp.api

import com.sealstudios.pokemonApp.api.objects.PokemonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


public interface PokemonService {

    @GET("pokemon")
    suspend fun getPokemon(): PokemonResponse

    @GET("/pokemon/{id}")
    suspend fun getPokemonById(@Path(value = "id") pokemonId: Int): PokemonResponse

    @GET("/pokemon/{name}")
    suspend fun getPokemonByName(@Path(value = "name") pokemonName: String): PokemonResponse

}