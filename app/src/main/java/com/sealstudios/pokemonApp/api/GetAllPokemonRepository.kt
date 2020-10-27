package com.sealstudios.pokemonApp.api

import com.sealstudios.pokemonApp.api.`object`.PokemonListResponse
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response


class GetAllPokemonRepository(
    private val remotePokemonRepository: RemotePokemonRepository,
    private val pokemonRepository: PokemonRepository,
    private val pokemonTypeRepository: PokemonTypeRepository,
    private val pokemonTypeJoinRepository: PokemonTypeJoinRepository,
    private val pokemonSpeciesRepository: PokemonSpeciesRepository,
    private val pokemonSpeciesJoinRepository: PokemonSpeciesJoinRepository,
//    private val pokemonMoveRepository: PokemonMoveRepository,
//    private val pokemonMoveJoinRepository: PokemonMoveJoinRepository
) {

    suspend fun getAllPokemonResponse(): Response<PokemonListResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext remotePokemonRepository.getAllPokemonResponse()
        }
    }

    suspend fun fetchPokemonForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonRequest =
                remotePokemonRepository.pokemonById(remotePokemonId)
            pokemonRequest.let { pokemonResponse ->
                if (pokemonResponse.isSuccessful) {
                    pokemonResponse.body()?.let { pokemon ->
                        insertPokemonTypes(pokemon)
//                        insertPartialPokemonMove(pokemon)
                        insertPokemon(
                            Pokemon.mapDbPokemonFromPokemonResponse(pokemon)
                        )
                    }
                }
            }
        }
    }

    private suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        withContext(Dispatchers.IO) {
            pokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
            pokemonSpeciesJoinRepository.insertPokemonSpeciesJoin(
                PokemonSpeciesJoin(
                    remotePokemonId,
                    pokemonSpecies.id
                )
            )
        }
    }

    private suspend fun insertPokemon(pokemon: Pokemon) {
        withContext(Dispatchers.IO) {
            pokemonRepository.insertPokemon(pokemon)
        }
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon
    ) {
        withContext(Dispatchers.IO) {
            for (type in remotePokemon.types) {
                pokemonTypeRepository.insertPokemonType(
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
    }

//    private suspend fun insertPartialPokemonMove(
//        remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon
//    ) {
//        withContext(Dispatchers.IO) {
//            for (move in remotePokemon.moves) {
//                pokemonMoveRepository.insertPokemonMove(
//                    mapPartialRemotePokemonMoveToDatabasePokemonMove(move)
//                )
//                pokemonMoveJoinRepository.insertPokemonMovesJoin(
//                    PokemonMovesJoin(
//                        remotePokemon.id,
//                        Pokemon.getPokemonIdFromUrl(move.move.url)
//                    )
//                )
//            }
//        }
//    }

    suspend fun fetchSpeciesForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonSpeciesRequest =
                remotePokemonRepository.speciesForId(remotePokemonId)
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