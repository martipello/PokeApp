package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


class StatsViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    fun setPokemonId(pokemonId: Int) {
        savedStateHandle.set(POKEMON_ID, pokemonId)
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(POKEMON_ID)
    }

    companion object {
        const val POKEMON_ID: String = "pokemonId"
    }

}