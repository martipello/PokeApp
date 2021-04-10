package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


class PokemonInfoViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()
    var evolutionId: MutableLiveData<Int> = getEvolutionIdSavedState()

    fun setPokemonId(pokemonId: Int) {
        savedStateHandle.set(POKEMON_ID, pokemonId)
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(POKEMON_ID)
    }

    fun setEvolutionId(evolutionId: Int) {
        savedStateHandle.set(EVOLUTION_ID, evolutionId)
    }

    private fun getEvolutionIdSavedState(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(EVOLUTION_ID)
    }

    companion object {
        const val POKEMON_ID: String = "pokemonId"
        const val EVOLUTION_ID: String = "evolutionId"
    }


}