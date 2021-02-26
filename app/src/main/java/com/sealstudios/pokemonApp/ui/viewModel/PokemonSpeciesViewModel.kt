package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.repository.PokemonSpeciesRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers

class PokemonSpeciesViewModel @ViewModelInject constructor(
        private val remotePokemonRepository: RemotePokemonRepository,
        private val pokemonSpeciesRepository: PokemonSpeciesRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val pokemonSpecies: LiveData<Resource<PokemonSpecies>> = pokemonSpecies()

    private fun pokemonSpecies() = pokemonId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            val pokemonSpecies = pokemonSpeciesRepository.getSinglePokemonSpeciesByIdAsync(id)
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
                    val species = PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(
                            pokemonSpeciesRequest.data
                    )
                    pokemonSpeciesRepository.insertPokemonSpecies(pokemonId, species)
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
        this.pokemonId.value = pokemonId
        savedStateHandle.set(PokemonSpeciesViewModel.pokemonId, pokemonId)
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        val id = savedStateHandle.get<Int>(PokemonSpeciesViewModel.pokemonId) ?: -1
        return MutableLiveData(id)
    }

    companion object {
        private const val pokemonId: String = "pokemonId"
    }

}

