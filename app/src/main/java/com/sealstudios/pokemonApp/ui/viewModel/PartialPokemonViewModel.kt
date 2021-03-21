package com.sealstudios.pokemonApp.ui.viewModel

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.RemotePokemonToRoomPokemonRepository
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.api.notification.NotificationActionReceiver
import com.sealstudios.pokemonApp.api.notification.NotificationClickListener
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PartialPokemonViewModel @ViewModelInject constructor(
        private val remoteRepository: RemotePokemonToRoomPokemonRepository,
        private val repository: PokemonRepository,
        private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {

    val requestDownloadPermission: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val partialPokemonEvent: MutableLiveData<Boolean> = MutableLiveData()

    val onFetchedPartialPokemonData = Transformations.switchMap(partialPokemonEvent) {
        liveData(viewModelScope.coroutineContext + Dispatchers.Default) {
            if (!hasFetchedPartialPokemonData) {
                fetchPartialPokemonData()
            } else {
                emit(Resource.success(null))
            }
        }
    }

    private suspend fun LiveDataScope<Resource<Boolean>>.fetchPartialPokemonData() {
        withContext(Dispatchers.IO) {
            val response = remoteRepository.getAllPokemonResponse()
            when (response.status) {
                Status.SUCCESS -> {
                    setFetchedPartialPokemonData(true)
                    withContext(Dispatchers.Default) {
                        response.data?.let {
                            saveAllPokemon(it.results)
                        }
                    }
                    emit(Resource.success(true))
                    withContext(Dispatchers.Main) {
                        requestDownloadPermission.call()
                    }
                }
                Status.ERROR -> emit(Resource.error(response.message ?: "", null, response.code))
                Status.LOADING -> emit(Resource.loading(null))
            }
        }
    }

    private suspend fun saveAllPokemon(pokemonListResponseData: List<NamedApiResource>) =
            withContext(Dispatchers.IO) {
                pokemonListResponseData.forEach {
                    repository.insertPokemon(Pokemon.defaultPokemon(it))
                }
            }


    private fun setFetchedPartialPokemonData(hasFetched: Boolean) {
        sharedPreferenceHelper.setBool(
                SharedPreferenceHelper.hasFetchedPartialPokemonData,
                hasFetched
        )
    }

    fun startFetchAllPokemonTypesAndSpeciesWorkManager(context: Context) {
        registerReceiverWithClickListener(context)
        remoteRepository.startFetchAllPokemonTypesAndSpecies()
    }

    fun cancelFetchAllPokemonTypesAndSpeciesWorkManager() {
        remoteRepository.cancelFetchAllPokemonTypesAndSpecies()
    }

    init {
        triggerGetPartialPokemonEvent()
    }

    private fun triggerGetPartialPokemonEvent() {
        partialPokemonEvent.value = true
    }

    fun retryGetPartialPokemonEvent() {
        setFetchedPartialPokemonData(false)
        partialPokemonEvent.value = true
    }

    private fun registerReceiverWithClickListener(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationHelper.NOTIFICATION_ACTION_KEY)
        var notificationActionReceiver: NotificationActionReceiver? = null
        val notificationClickListener = object : NotificationClickListener {
            override fun cancelAction() {
                cancelFetchAllPokemonTypesAndSpeciesWorkManager()
                try {
                    context.unregisterReceiver(notificationActionReceiver)
                } catch (e: Exception) {
                    Log.d("cancelAction", "Exception: $e")
                }
            }
        }
        notificationActionReceiver = NotificationActionReceiver(notificationClickListener)
        context.registerReceiver(NotificationActionReceiver(notificationClickListener), intentFilter)
    }

    private val hasFetchedPartialPokemonData get() = sharedPreferenceHelper.getBool(SharedPreferenceHelper.hasFetchedPartialPokemonData, false)

}