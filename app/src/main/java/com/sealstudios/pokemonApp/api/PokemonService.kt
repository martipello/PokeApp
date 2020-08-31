package com.sealstudios.pokemonApp.api

import com.sealstudios.pokemonApp.api.`object`.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PokemonService {

    @GET("pokemon/")
    suspend fun getPokemon(
        @Query(value = "offset") offset: Int,
        @Query(value = "limit") limit: Int
    ): Response<PokemonListResponse>

    @GET("pokemon/")
    suspend fun getPokemonResponse(
        @Query(value = "offset") offset: Int,
        @Query(value = "limit") limit: Int
    ): Response<PokemonListResponse>

    @GET("pokemon/{id}/")
    suspend fun getPokemonById(
        @Path(value = "id") pokemonId: Int,
        @Query(value = "offset") offset: Int,
        @Query(value = "limit") limit: Int
    ): Response<Pokemon>

    @GET("pokemon/{name}/")
    suspend fun getPokemonByName(
        @Path(value = "name") pokemonName: String,
        @Query(value = "offset") offset: Int,
        @Query(value = "limit") limit: Int
    ): Response<Pokemon>

    @GET("pokemon-species/{id}/")
    suspend fun getPokemonSpeciesForId(
        @Path(value = "id") speciesId: Int
    ): Response<PokemonSpecies>

    @GET("ability/{id}/")
    suspend fun getPokemonAbilityForId(
        @Path(value = "id") abilityId: Int
    ): Response<PokemonAbility>

    @GET("move/{id}/")
    suspend fun getPokemonMoveForId(
        @Path(value = "id") moveId: Int
    ): Response<PokemonMove>

    @GET("evolution-chain/{id}/")
    suspend fun getPokemonEvolutionsForId(
        @Path(value = "id") moveId: Int
    ): Response<EvolutionChain>

}