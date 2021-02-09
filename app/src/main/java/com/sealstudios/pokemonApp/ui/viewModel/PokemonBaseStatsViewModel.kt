package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.database.`object`.PokemonWithBaseStats
import com.sealstudios.pokemonApp.repository.PokemonBaseStatsRepository

class PokemonBaseStatsViewModel @ViewModelInject constructor(
        private val pokemonBaseStatsRepository: PokemonBaseStatsRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val pokemonWithStats: LiveData<Resource<PokemonWithBaseStats>> = pokemonId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            emit(Resource.success(pokemonBaseStatsRepository.getPokemonWithStatsByIdAsync(pokemonId = id)))
        }
    }

    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
        savedStateHandle.set(PokemonBaseStatsViewModel.pokemonId, pokemonId)
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        val id = savedStateHandle.get<Int>(PokemonBaseStatsViewModel.pokemonId) ?: -1
        return MutableLiveData(id)
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

    companion object {
        private const val pokemonId: String = "pokemonId"
    }

}

