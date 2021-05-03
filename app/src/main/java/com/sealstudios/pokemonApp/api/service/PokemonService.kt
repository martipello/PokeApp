package com.sealstudios.pokemonApp.api.service

import com.sealstudios.PokemonQuery
import com.sealstudios.pokemonApp.api.`object`.*
import io.github.wax911.library.annotation.GraphQuery
import io.github.wax911.library.model.request.QueryContainerBuilder
import retrofit2.Response
import retrofit2.http.*


interface PokemonService {

    @GET("pokemon/")
    suspend fun getPokemon(
            @Query(value = "offset") offset: Int,
            @Query(value = "limit") limit: Int
    ): PokemonListResponse

    @GET("pokemon/{id}/")
    suspend fun getPokemonForId(
            @Path(value = "id") pokemonId: Int,
            @Query(value = "offset") offset: Int,
            @Query(value = "limit") limit: Int
    ): ApiPokemon

    @GET("type/{id}/")
    suspend fun getPokemonTypeForId(
            @Path(value = "id") typeId: Int
    ): Type

    @GET("pokemon-species/{id}/")
    suspend fun getPokemonSpeciesForId(
            @Path(value = "id") speciesId: Int
    ): ApiPokemonSpecies

    @GET("move/{id}/")
    suspend fun getPokemonMoveForId(
            @Path(value = "id") moveId: Int
    ): ApiPokemonMove

    @GET("ability/{id}/")
    suspend fun getAbilityForId(
            @Path(value = "id") abilityId: Int
    ): Ability

    @GET("evolution-chain/{id}/")
    suspend fun getPokemonEvolutionsForId(
            @Path(value = "id") evolutionChainId: Int
    ): EvolutionChain

}