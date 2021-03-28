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

    private var evolutionId: MutableLiveData<Int> = getPokemonIdSavedState()

    val evolution: LiveData<Resource<EvolutionChainWithDetailList>> = pokemonEvolution()

    private fun pokemonEvolution() = evolutionId.switchMap { id ->
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

    private suspend fun fetchPokemonEvolutionChain(evolutionId: Int) = liveData(Dispatchers.IO) {
        val evolutionChainRequest = remotePokemonRepository.evolutionChainForId(evolutionId)
        when (evolutionChainRequest.status) {
            Status.SUCCESS -> {
                if (evolutionChainRequest.data != null) {
                    evolutionRepository.insertPokemonEvolution(evolutionChainRequest.data)
                    val evolutionChain = evolutionRepository.getPokemonEvolutionChainWithDetailListByIdAsync(evolutionId)
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

    fun setEvolutionId(pokemonId: Int) {
        savedStateHandle.set(evolutionIdKey, pokemonId)
    }

    fun retry() {
        this.evolutionId.value = this.evolutionId.value
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(evolutionIdKey)
    }

    companion object {
        private const val evolutionIdKey: String = "evolutionId"
    }

}

