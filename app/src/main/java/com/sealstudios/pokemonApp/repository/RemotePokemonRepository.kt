package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.*
import com.sealstudios.pokemonApp.api.service.PokemonService
import retrofit2.Response
import javax.inject.Inject
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon


class RemotePokemonRepository @Inject constructor(
    private val pokemonService: PokemonService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getAllPokemonResponse(): Resource<PokemonListResponse> {
        return try {
            responseHandler.handleSuccess(pokemonService.getPokemon(offset = 0, limit = 1050))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun pokemonById(id: Int): Response<apiPokemon> {
        return pokemonService.getPokemonById(id, offset = 0, limit = 1)
    }

    suspend fun speciesForId(id: Int): Response<PokemonSpecies> {
        return pokemonService.getPokemonSpeciesForId(id)
    }

    suspend fun moveForId(id: Int): Response<PokemonMove> {
        return pokemonService.getPokemonMoveForId(id)
    }

    suspend fun getRemotePokemonAbilitiesForId(id: Int): Response<PokemonAbility> {
        return pokemonService.getPokemonAbilityForId(id)
    }

    suspend fun getRemotePokemonEvolutionChainForId(id: Int): Response<PokemonSpecies> {
        return pokemonService.getPokemonSpeciesForId(id)
    }

}

