package com.sealstudios.pokemonApp.api

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sealstudios.pokemonApp.api.`object`.*
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.repository.PokemonSpeciesRepository
import com.sealstudios.pokemonApp.repository.PokemonTypeRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemotePokemonToRoomPokemonRepository @Inject constructor(
    private val workManager: WorkManager,
    private val remotePokemonRepository: RemotePokemonRepository,
    private val pokemonRepository: PokemonRepository,
    private val pokemonTypeRepository: PokemonTypeRepository,
    private val pokemonSpeciesRepository: PokemonSpeciesRepository,
) {

    fun startFetchAllPokemonTypesAndSpecies() {
        workManager.enqueue(OneTimeWorkRequest.from(PokemonWorkManager::class.java))
    }

    suspend fun getAllPokemonResponse(): Resource<PokemonListResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext remotePokemonRepository.getAllPokemonResponse()
        }
    }

    suspend fun fetchAndSaveSpeciesForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonSpeciesRequest =
                remotePokemonRepository.speciesForId(remotePokemonId)
            when (pokemonSpeciesRequest.status) {
                Status.SUCCESS -> pokemonSpeciesRequest.data?.let {
                    insertPokemonSpecies(
                        remotePokemonId,
                        PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(it)
                    )
                }
                else -> {}
            }
        }
    }

    suspend fun fetchAndSavePokemonForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonRequest =
                remotePokemonRepository.pokemonForId(remotePokemonId)
            when (pokemonRequest.status) {
                Status.SUCCESS -> {
                    pokemonRequest.data?.let {
                        insertPokemonTypes(it)
                    }
                }
                else -> {}
            }

        }
    }

    private suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        withContext(Dispatchers.IO) {
            pokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
            pokemonSpeciesRepository.insertPokemonSpeciesJoin(
                PokemonSpeciesJoin(
                    remotePokemonId,
                    pokemonSpecies.id
                )
            )
        }
    }

    suspend fun saveAllPokemon(pokemonListResponseData: List<NamedApiResource>) =
        withContext(Dispatchers.IO) {
            pokemonListResponseData.map {
                insertPokemon(
                    Pokemon.defaultPokemon(it)
                )
            }
        }

    private suspend fun insertPokemon(pokemon: Pokemon) {
        withContext(Dispatchers.IO) {
            pokemonRepository.insertPokemon(pokemon)
        }
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {
            remotePokemon.types?.map {
                val pokemonType = PokemonType(
                    Pokemon.getPokemonIdFromUrl(it.type.url),
                    it.type.name,
                    it.slot
                )
                val pokemonTypeJoin = PokemonTypesJoin(
                    remotePokemon.id,
                    Pokemon.getPokemonIdFromUrl(it.type.url)
                )
                pokemonTypeRepository.insertPokemonType(
                    pokemonType
                )
                pokemonTypeRepository.insertPokemonTypeJoin(
                    pokemonTypeJoin
                )
            }
        }
    }
}