package com.sealstudios.pokemonApp.api

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_ID
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_NAME
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.repository.PokemonTypeJoinRepository
import com.sealstudios.pokemonApp.repository.PokemonTypeRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.*


class GetAllPokemonWorkManager @WorkerInject constructor(
    @Assisted @NonNull context: Context,
    @Assisted @NonNull params: WorkerParameters,
    private val remotePokemonRepository: RemotePokemonRepository,
    private val localPokemonRepository: PokemonRepository,
    private val localPokemonTypeRepository: PokemonTypeRepository,
    private val pokemonTypeJoinRepository: PokemonTypeJoinRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        withContext(context = Dispatchers.IO, block = {
            val progress = "Starting Download"
            setForeground(
                notificationHelper.sendOnGoingNotification(
                    NOTIFICATION_ID,
                    NOTIFICATION_NAME,
                    progress,
                    0,
                    100
                )
            )
            getRemotePokemon(
                worker = this@GetAllPokemonWorkManager,
                remoteRepository = remotePokemonRepository,
                localRepository = localPokemonRepository,
                coroutineScope = this
            )
        })
        Result.success()
    }


    private fun getRemotePokemon(
        worker: CoroutineWorker,
        remoteRepository: RemotePokemonRepository,
        localRepository: PokemonRepository,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            val pokemonResponse = async { remoteRepository.fetchPokemon() }
            pokemonResponse.await().let { response ->
                if (response.isSuccessful) {
                    response.body()?.results?.let { results ->
                        for (i in 0 until results.size) {
                            results[i]?.let { result ->
                                val pokemonDetailRequest = async {
                                    remoteRepository.getRemotePokemonById(
                                        Pokemon.getPokemonIdFromUrl(result.url)
                                    )
                                }
                                pokemonDetailRequest.await().let { detailResponse ->
                                    if (detailResponse.isSuccessful) {
                                        detailResponse.body()?.let { remotePokemon ->
                                            val localPokemon =
                                                Pokemon.buildDbPokemonFromPokemonResponse(
                                                    remotePokemon
                                                )
                                            fetchAndApplySpecies(
                                                remoteRepository,
                                                remotePokemon,
                                                localPokemon
                                            )
                                            insertPokemonTypes(remotePokemon)
                                            localRepository.insertPokemon(localPokemon)
                                            setForeGroundAsync(
                                                worker, "$i of ${results.size}", i + 1, results.size
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun insertPokemonTypes(remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon) {
        for (type in remotePokemon.types){
            localPokemonTypeRepository.insertPokemonType(
                PokemonType(
                    getPokemonIdFromUrl(type.type.url),
                    type.type.name,
                    type.slot
                )
            )
            pokemonTypeJoinRepository.insertPokemonTypeJoin(
                PokemonTypesJoin(
                    remotePokemon.id,
                    getPokemonIdFromUrl(type.type.url)
                )
            )
        }
    }

    private suspend fun CoroutineScope.fetchAndApplySpecies(
        remoteRepository: RemotePokemonRepository,
        remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon,
        localPokemon: Pokemon
    ) {
        val pokemonSpeciesRequest = async {
            remoteRepository.getRemotePokemonSpeciesForId(
                remotePokemon.id
            )
        }
        pokemonSpeciesRequest.await()
            .let { pokemonSpeciesResponse ->
                if (pokemonSpeciesResponse.isSuccessful) {
                    pokemonSpeciesResponse.body()
                        ?.let { species ->
                            localPokemon.apply {
                                this.species =
                                    species.genera.first { it.language.name == "en" }.genus
                            }
                        }
                }
            }
    }

    private fun setForeGroundAsync(
        worker: CoroutineWorker,
        progressText: String,
        progress: Int,
        max: Int
    ) {
        worker.setForegroundAsync(
            notificationHelper.sendOnGoingNotification(
                NOTIFICATION_ID,
                NOTIFICATION_NAME,
                progressText,
                progress,
                max
            )
        )
    }


}