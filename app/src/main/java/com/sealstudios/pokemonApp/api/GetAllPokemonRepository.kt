package com.sealstudios.pokemonApp.api

import android.util.Log
import com.sealstudios.pokemonApp.api.`object`.PokemonListResponse
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.*
import kotlinx.coroutines.coroutineScope
import retrofit2.Response


class GetAllPokemonRepository(
    private val remotePokemonRepository: RemotePokemonRepository,
    private val localPokemonRepository: PokemonRepository,
    private val localPokemonTypeRepository: PokemonTypeRepository,
    private val pokemonTypeJoinRepository: PokemonTypeJoinRepository,
    private val localPokemonSpeciesRepository: PokemonSpeciesRepository,
    private val pokemonSpeciesJoinRepository: PokemonSpeciesJoinRepository
) {

    suspend fun getPokemonResponse(): Response<PokemonListResponse> {
        return remotePokemonRepository.fetchPokemon()
    }

    suspend fun fetchPokemonForId(
        remotePokemonId: Int
    ) {
        coroutineScope {
            val pokemonRequest =
                remotePokemonRepository.getRemotePokemonById(remotePokemonId)
            pokemonRequest.let { pokemonResponse ->
                if (pokemonResponse.isSuccessful) {
                    pokemonResponse.body()?.let { pokemon ->
                        insertPokemonTypes(pokemon)
                        insertPokemon(
                            Pokemon.mapDbPokemonFromPokemonResponse(pokemon)
                        )
                    }
                }
            }
        }
    }

    private suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        localPokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
        pokemonSpeciesJoinRepository.insertPokemonSpeciesJoin(
            PokemonSpeciesJoin(
                remotePokemonId,
                pokemonSpecies.id
            )
        )
    }

    private suspend fun insertPokemon(pokemon: Pokemon) {
        localPokemonRepository.insertPokemon(pokemon)
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon) {
        for (type in remotePokemon.types) {
            localPokemonTypeRepository.insertPokemonType(
                PokemonType(
                    Pokemon.getPokemonIdFromUrl(type.type.url),
                    type.type.name,
                    type.slot
                )
            )
            pokemonTypeJoinRepository.insertPokemonTypeJoin(
                PokemonTypesJoin(
                    remotePokemon.id,
                    Pokemon.getPokemonIdFromUrl(type.type.url)
                )
            )
        }
    }

    suspend fun fetchSpeciesForId(
        remotePokemonId: Int
    ) {
        coroutineScope {
            val pokemonSpeciesRequest =
                remotePokemonRepository.getRemotePokemonSpeciesForId(remotePokemonId)
            pokemonSpeciesRequest.let { pokemonSpeciesResponse ->
                if (pokemonSpeciesResponse.isSuccessful) {
                    pokemonSpeciesResponse.body()?.let { species ->
                        insertPokemonSpecies(
                            remotePokemonId,
                            PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(species)
                        )
                    }
                }
            }

        }
    }

}