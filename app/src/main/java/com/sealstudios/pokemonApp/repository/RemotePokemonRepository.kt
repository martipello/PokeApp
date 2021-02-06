package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.*
import com.sealstudios.pokemonApp.api.service.PokemonService
import javax.inject.Inject


class RemotePokemonRepository @Inject constructor(
    private val pokemonService: PokemonService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getAllPokemonResponse(): Resource<PokemonListResponse> {
        return try {
            responseHandler.handleSuccess(pokemonService.getPokemon(offset = 0, limit = 150))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun pokemonForId(id: Int): Resource<ApiPokemon> {
        return try {
            responseHandler.handleSuccess(pokemonService.getPokemonForId(id, offset = 0, limit = 1))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun speciesForId(id: Int): Resource<ApiPokemonSpecies> {
        return try {
            responseHandler.handleSuccess(pokemonService.getPokemonSpeciesForId(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun moveForId(id: Int): Resource<ApiPokemonMove> {
        return try {
            responseHandler.handleSuccess(pokemonService.getPokemonMoveForId(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getRemotePokemonEvolutionChainForId(id: Int): Resource<EvolutionChain> {
        return try {
            responseHandler.handleSuccess(pokemonService.getPokemonEvolutionsForId(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun abilityForId(id: Int): Resource<Ability> {
        return try {
            responseHandler.handleSuccess(pokemonService.getAbilityForId(id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }


}

