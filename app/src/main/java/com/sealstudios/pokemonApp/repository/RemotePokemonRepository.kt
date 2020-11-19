package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.service.PokemonService
import com.sealstudios.pokemonApp.api.`object`.PokemonAbility
import com.sealstudios.pokemonApp.api.`object`.PokemonListResponse
import com.sealstudios.pokemonApp.api.`object`.PokemonMove
import com.sealstudios.pokemonApp.api.`object`.PokemonSpecies
import retrofit2.Response
import javax.inject.Inject
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon


class RemotePokemonRepository @Inject constructor(
    private val pokemonService: PokemonService
) {

    suspend fun getAllPokemonResponse(): Response<PokemonListResponse> {
        return pokemonService.getPokemonResponse(offset = 0, limit = 1050)
    }

    suspend fun getAllPokemon(): PokemonListResponse {
        return pokemonService.getPokemon(offset = 0, limit = 1100)
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

