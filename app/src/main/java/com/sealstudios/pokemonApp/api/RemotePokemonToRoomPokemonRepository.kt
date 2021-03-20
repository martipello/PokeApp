package com.sealstudios.pokemonApp.api

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sealstudios.pokemonApp.api.`object`.*
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemotePokemonToRoomPokemonRepository @Inject constructor(
        private val workManager: WorkManager,
        private val remotePokemonRepository: RemotePokemonRepository,
        private val pokemonRepository: PokemonRepository,
        private val pokemonTypeRepository: PokemonTypeRepository,
        private val pokemonTypeMetaDataRepository: PokemonTypeMetaDataRepository,
        private val pokemonSpeciesRepository: PokemonSpeciesRepository,
) {

    fun startFetchAllPokemonTypesAndSpecies() {
        workManager.enqueue(OneTimeWorkRequest.from(PokemonWorkManager::class.java))
    }

    fun cancelFetchAllPokemonTypesAndSpecies() {
        workManager.cancelAllWork()
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
                    pokemonSpeciesRepository.insertPokemonSpecies(
                            remotePokemonId,
                            PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(it)
                    )
                }
                else -> {
                }
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
                        pokemonTypeRepository.insertPokemonTypes(it)
                        pokemonTypeMetaDataRepository.insertPokemonTypeMetaData(it)
                    }
                }
                else -> {
                }
            }
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

}