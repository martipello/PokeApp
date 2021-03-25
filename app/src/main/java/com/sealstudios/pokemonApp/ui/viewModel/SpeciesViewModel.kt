package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Species
import com.sealstudios.pokemonApp.repository.SpeciesRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers

class SpeciesViewModel @ViewModelInject constructor(
        private val remotePokemonRepository: RemotePokemonRepository,
        private val speciesRepository: SpeciesRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val species: LiveData<Resource<Species>> = pokemonSpecies()

    private fun pokemonSpecies() = pokemonId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            val pokemonSpecies = speciesRepository.getSinglePokemonSpeciesByIdAsync(id)
            if (pokemonSpecies == null) {
                emitSource(fetchPokemonSpecies(id))
            } else {
                emit(
                        Resource.success(
                                pokemonSpecies
                        )
                )
            }
        }
    }

    private suspend fun fetchPokemonSpecies(pokemonId: Int) = liveData(Dispatchers.IO) {
        val pokemonSpeciesRequest = remotePokemonRepository.speciesForId(pokemonId)
        when (pokemonSpeciesRequest.status) {
            Status.SUCCESS -> {
                if (pokemonSpeciesRequest.data != null) {
                    val species = Species.mapRemotePokemonSpeciesToDatabasePokemonSpecies(
                            pokemonSpeciesRequest.data
                    )
                    speciesRepository.insertPokemonSpecies(pokemonId, species)
                    emit(Resource.success(species))
                } else {
                    emit(Resource.error(pokemonSpeciesRequest.message
                            ?: "Data is empty", null, pokemonSpeciesRequest.code))
                }
            }
            Status.ERROR -> emit(
                    Resource.error(
                            pokemonSpeciesRequest.message ?: "General error",
                            null, pokemonSpeciesRequest.code
                    )
            )
            Status.LOADING -> emit(Resource.loading(null))
        }

    }

    fun setPokemonId(pokemonId: Int) {
        savedStateHandle.set(SpeciesViewModel.pokemonId, pokemonId)
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(SpeciesViewModel.pokemonId)
    }

    companion object {
        private const val pokemonId: String = "pokemonId"
    }

}

