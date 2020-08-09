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
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import kotlinx.coroutines.*


class GetAllPokemonWorkManager @WorkerInject constructor(
    @Assisted @NonNull context: Context,
    @Assisted @NonNull params: WorkerParameters,
    private val pokemonRepository: PokemonRepository,
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
                repository = pokemonRepository,
                coroutineScope = this
            )
        })
        Result.success()
    }


    private fun getRemotePokemon(
        worker: CoroutineWorker,
        repository: PokemonRepository,
        coroutineScope: CoroutineScope
    ) {
        val tag = "getRemotePokemon"
        coroutineScope.launch {
            val pokemonResponse = repository.getRemotePokemon().body()
            pokemonResponse?.results?.let { pokemonResponseResult ->
                for (i in 0 until pokemonResponseResult.size) {
                    pokemonResponseResult[i]?.url?.let { pokemonUrl ->
                        try {
                            val pokemonId = Pokemon.getPokemonIdFromUrl(pokemonUrl)
                            when {
                                pokemonId >= 0 -> {
                                    val pokemonRequest =
                                        async { repository.getRemotePokemonById(pokemonId) }
                                    pokemonRequest.await().body()?.let { pokemon ->
                                        val dbPokemon =
                                            Pokemon.buildDbPokemonFromPokemonResponse(pokemon)
                                        setForegroundAsync(worker, i + 1, pokemonResponseResult.size)
                                        PokemonDetailViewModel.getRemotePokemonDetail(
                                            coroutineScope,
                                            dbPokemon,
                                            repository = repository
                                        )
                                    }
                                }
                                else -> {
                                }
                            }
                        } catch (exception: Exception) {
                            exception.message?.let {
                                Log.e(tag, it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setForegroundAsync(
        worker: CoroutineWorker,
        i: Int,
        max: Int
    ) {
        worker.setForegroundAsync(
            notificationHelper.sendOnGoingNotification(
                NOTIFICATION_ID,
                NOTIFICATION_NAME,
                "$i of $max",
                i,
                max
            )
        )
    }


}