package com.sealstudios.pokemonApp.api

import android.content.Context
import androidx.annotation.NonNull
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_ID
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_NAME
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class PokemonWorkManager @WorkerInject constructor(
        @Assisted @NonNull context: Context,
        @Assisted @NonNull params: WorkerParameters,
        private val remotePokemonToRoomPokemonHelper: RemotePokemonToRoomPokemonRepository,
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
            handleAllPokemonResponse(
                    worker = this@PokemonWorkManager
            )
        })
        Result.success()
    }

    private suspend fun handleAllPokemonResponse(worker: CoroutineWorker) = withContext(context = Dispatchers.IO) {
        val pokemonRefListResponse = remotePokemonToRoomPokemonHelper.getAllPokemonResponse()
        when (pokemonRefListResponse.status) {
            Status.SUCCESS -> {
                pokemonRefListResponse.data?.let {
                    saveAllPokemon(it.results, worker)
                    savePokemonTypesAndSpecies(it.results, worker)
                }
            }
            Status.ERROR -> {
            }
            Status.LOADING -> {
            }
        }
    }

    private suspend fun saveAllPokemon(
            data: List<NamedApiResource>,
            worker: CoroutineWorker
    ) =
            withContext(Dispatchers.IO) {
                remotePokemonToRoomPokemonHelper.saveAllPokemon(data)
                setForeGroundAsync(
                        worker,
                        "Downloaded partial pokedex data",
                        100,
                        100,
                        true
                )
            }

    private suspend fun savePokemonTypesAndSpecies(
            data: List<NamedApiResource>,
            worker: CoroutineWorker
    ) =
            withContext(Dispatchers.IO) {
                for (i in data.indices) {
                    data[i].let { pokemonRef ->
                        val id = getPokemonIdFromUrl(pokemonRef.url)
                        val fetchPokemonAsync =
                                async { remotePokemonToRoomPokemonHelper.fetchAndSavePokemonForId(id) }
                        val fetchSpeciesAsync =
                                async { remotePokemonToRoomPokemonHelper.fetchAndSaveSpeciesForId(id) }
                        fetchSpeciesAsync.await()
                        fetchPokemonAsync.await()
                        setForeGroundAsync(
                                worker, "$i of ${data.size}", i + 1, data.size, false
                        )
                    }
                }
            }

    private fun setForeGroundAsync(
            worker: CoroutineWorker,
            progressText: String,
            progress: Int,
            max: Int,
            indeterminate: Boolean
    ) {
        worker.setForegroundAsync(
                notificationHelper.sendOnGoingNotification(
                        NOTIFICATION_ID,
                        NOTIFICATION_NAME,
                        progressText,
                        progress,
                        max,
                        indeterminate
                )
        )
    }

}