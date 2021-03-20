package com.sealstudios.pokemonApp.api

import android.content.Context
import androidx.annotation.NonNull
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.api.notification.NotificationArguments
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_ID
import com.sealstudios.pokemonApp.api.notification.NotificationHelper.Companion.NOTIFICATION_NAME
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class PokemonWorkManager @WorkerInject constructor(
        @Assisted @NonNull context: Context,
        @Assisted @NonNull params: WorkerParameters,
        private val remotePokemonToRoomPokemonHelper: RemotePokemonToRoomPokemonRepository,
        private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        withContext(context = Dispatchers.IO, block = {
            val notificationArguments = createNotificationArguments(
                    "Starting Download",
                    0,
                    100,
                    true)
            setForeground(
                    notificationHelper.sendFetchAllPokemonDataNotification(
                            notificationArguments
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
            data: List<NamedApiResource>, worker: CoroutineWorker) = withContext(Dispatchers.IO) {
                remotePokemonToRoomPokemonHelper.saveAllPokemon(data)
                val notificationArguments = createNotificationArguments("Downloaded partial pokedex data", 100, 100, true)
                setForeGroundAsync(
                        worker,
                        notificationArguments
                )
            }

    private suspend fun savePokemonTypesAndSpecies(
            data: List<NamedApiResource>, worker: CoroutineWorker) = withContext(Dispatchers.IO) {
                for (i in data.indices) {
                    data[i].let { pokemonRef ->
                        val id = getPokemonIdFromUrl(pokemonRef.url)
                        val fetchPokemonAsync =
                                async { remotePokemonToRoomPokemonHelper.fetchAndSavePokemonForId(id) }
                        val fetchSpeciesAsync =
                                async { remotePokemonToRoomPokemonHelper.fetchAndSaveSpeciesForId(id) }
                        fetchSpeciesAsync.await()
                        fetchPokemonAsync.await()
                        val notificationArguments = createNotificationArguments("$i of ${data.size}", i, data.size, false)
                        setForeGroundAsync(
                                worker, notificationArguments
                        )
                    }
                }
            }

    private fun createNotificationArguments(progressText: String, progress: Int, progressMax: Int, isIndeterminate: Boolean): NotificationArguments {
        return NotificationArguments(
                NOTIFICATION_ID,
                NOTIFICATION_NAME,
                progressText,
                progress + 1,
                progressMax,
                isIndeterminate)
    }

    private fun setForeGroundAsync(
            worker: CoroutineWorker,
            notificationArguments: NotificationArguments
    ) {
        worker.setForegroundAsync(
                notificationHelper.sendFetchAllPokemonDataNotification(
                        notificationArguments
                )
        )
    }

}