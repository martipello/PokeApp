package com.sealstudios.pokemonApp.api

import android.content.Context
import androidx.annotation.NonNull
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_ID
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_NAME
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
import kotlinx.coroutines.*

class GetAllPokemonWorkManager @WorkerInject constructor(
    @Assisted @NonNull context: Context,
    @Assisted @NonNull params: WorkerParameters,
    private val allPokemonRepository: GetAllPokemonRepository,
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
                worker = this@GetAllPokemonWorkManager
            )
        })
        Result.success()
    }


    private suspend fun getRemotePokemon(
        worker: CoroutineWorker,
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonRefListResponse = allPokemonRepository.getAllPokemonResponse()
            if (pokemonRefListResponse.isSuccessful) {
                pokemonRefListResponse.body()?.results?.let { pokemonRefList ->
                    for (i in 0 until pokemonRefList.size) {
                        pokemonRefList[i]?.let { pokemonRef ->
                            val id = getPokemonIdFromUrl(pokemonRef.url)
                            val fetchSpecies = async { allPokemonRepository.fetchSpeciesForId(id) }
                            val fetchPokemon = async { allPokemonRepository.fetchPokemonForId(id) }
                            fetchPokemon.await()
                            fetchSpecies.await()
                            setForeGroundAsync(
                                worker, "$i of ${pokemonRefList.size}", i + 1, pokemonRefList.size
                            )
                        }
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