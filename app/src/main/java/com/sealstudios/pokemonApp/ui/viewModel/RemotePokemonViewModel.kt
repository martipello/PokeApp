package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.RemotePokemonToRoomPokemonRepository
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers

class RemotePokemonViewModel @ViewModelInject constructor(
    private val remoteRepository: RemotePokemonToRoomPokemonRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper

) : ViewModel() {

    private val fetchAllPokemonAction: MutableLiveData<Boolean> = MutableLiveData()

    val allPokemonResponse = fetchAllPokemonAction.switchMap {
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (!sharedPreferenceHelper.getBool(SharedPreferenceHelper.hasSuccessFullyFetchedPartialPokemonData, false)) {
                emit(Resource.loading(null))
                val response = remoteRepository.getAllPokemonResponse()
                when (response.status) {
                    Status.SUCCESS -> {
                        sharedPreferenceHelper.setBool(
                            SharedPreferenceHelper.hasSuccessFullyFetchedPartialPokemonData,
                            true
                        )
                        emit(Resource.success(response.data))
                        remoteRepository.startFetchAllPokemonTypesAndSpecies()
                    }
                    Status.ERROR -> emit(Resource.error(response.message ?: "", response.data))
                    Status.LOADING -> emit(Resource.loading(response.data))
                }
            } else {
                emit(Resource.success(null))
            }

        }
    }

    init {
        fetchAllPokemon()
    }

    fun fetchAllPokemon() {
        fetchAllPokemonAction.value = true
    }

}