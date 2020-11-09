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
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.highResPokemonUrl
import kotlinx.coroutines.*

class GetAllPokemonWorkManager @WorkerInject constructor(
    @Assisted @NonNull context: Context,
    @Assisted @NonNull params: WorkerParameters,
    private val allPokemonHelper: GetAllPokemonHelper,
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
            val pokemonRefListResponse = allPokemonHelper.getAllPokemonResponse()
            if (pokemonRefListResponse.isSuccessful) {
                pokemonRefListResponse.body()?.results?.let { pokemonRefList ->
                    val saveAll = async {
                        allPokemonHelper.insertPokemon(pokemonRefList.map {
                            val id = getPokemonIdFromUrl(it.url)
                            Pokemon(
                                id = id,
                                name = it.name,
                                image = highResPokemonUrl(id),
                                height = 0,
                                weight = 0,
                                move_ids = listOf(),
                                versionsLearnt = listOf(),
                                learnMethods = listOf(),
                                levelsLearnedAt = listOf(),
                                sprite = "",
                            )
                        })
                    }
                    Log.d("POKE_APP", "await")
                    saveAll.await()
                    Log.d("POKE_APP", "awaited")

                    setForeGroundAsync(
                        worker, "Downloaded partial pokedex data", 100, 100
                    )

//        TODO make this part of a paging data request

//                    for (i in pokemonRefList.indices) {
//                        Log.d("WORKER", "pokemonRefList $i")
//                        pokemonRefList[i].let { pokemonRef ->
//                            val id = getPokemonIdFromUrl(pokemonRef.url)
//                            val fetchPokemon = async { allPokemonHelper.fetchPokemonForId(id) }
//                            async { allPokemonHelper.fetchSpeciesForId(id) }
//                            fetchPokemon.await()
////                            fetchSpecies.await()
//                            setForeGroundAsync(
//                                worker, "$i of ${pokemonRefList.size}", i + 1, pokemonRefList.size
//                            )
//                        }
//                    }

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