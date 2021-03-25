package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.relations.EvolutionChainWithDetailList
import com.sealstudios.pokemonApp.repository.EvolutionRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers

class EvolutionViewModel @ViewModelInject constructor(
        private val remotePokemonRepository: RemotePokemonRepository,
        private val evolutionRepository: EvolutionRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val evolution: LiveData<Resource<EvolutionChainWithDetailList>> = pokemonEvolution()

    private fun pokemonEvolution() = pokemonId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            val pokemonEvolution = evolutionRepository.getPokemonEvolutionChainWithDetailListByIdAsync(id)
            if (pokemonEvolution == null) {
                emitSource(fetchPokemonEvolutionChain(id))
            } else {
                emit(
                        Resource.success(
                                pokemonEvolution
                        )
                )
            }
        }
    }

    private suspend fun fetchPokemonEvolutionChain(pokemonId: Int) = liveData(Dispatchers.IO) {
        val evolutionChainRequest = remotePokemonRepository.evolutionChainForId(pokemonId)
        when (evolutionChainRequest.status) {
            Status.SUCCESS -> {
                if (evolutionChainRequest.data != null) {
                    evolutionRepository.insertPokemonEvolution(evolutionChainRequest.data)
                    val evolutionChain = evolutionRepository.getPokemonEvolutionChainWithDetailListByIdAsync(pokemonId)
                    emit(Resource.success(evolutionChain))
                } else {
                    emit(Resource.error(evolutionChainRequest.message
                            ?: "Data is empty", null, evolutionChainRequest.code))
                }
            }
            Status.ERROR -> emit(
                    Resource.error(
                            evolutionChainRequest.message ?: "General error",
                            null, evolutionChainRequest.code
                    )
            )
            Status.LOADING -> emit(Resource.loading(null))
        }

    }

    fun setPokemonId(pokemonId: Int) {
        savedStateHandle.set(pokemonIdKey, pokemonId)
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(pokemonIdKey)
    }

    companion object {
        private const val pokemonIdKey: String = "pokemonId"
    }

}

