package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityJoin
import com.sealstudios.pokemonApp.repository.PokemonAbilityRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonAbilityViewModel @ViewModelInject constructor(
    private val remotePokemonRepository: RemotePokemonRepository,
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var abilityId: MutableLiveData<Int> = getAbilityIdSavedState()

    val pokemonAbility: LiveData<Resource<PokemonAbility>> = pokemonAbility()

    private fun pokemonAbility() = abilityId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            val pokemonAbility = pokemonAbilityRepository.getPokemonAbilityByIdAsync(id)
            if (pokemonAbility == null) {
                emitSource(fetchPokemonAbility(id))
            } else {
                emit(
                    Resource.success(
                        pokemonAbility
                    )
                )
            }
        }
    }

    private suspend fun fetchPokemonAbility(pokemonId: Int) = liveData(Dispatchers.IO) {
        val pokemonAbilityRequest = remotePokemonRepository.getRemotePokemonAbilityForId(pokemonId)
        when (pokemonAbilityRequest.status) {
            Status.SUCCESS -> {
                if (pokemonAbilityRequest.data != null) {
                    val ability = PokemonAbility.mapRemotePokemonAbilityToDatabasePokemonAbility(
                        pokemonAbilityRequest.data
                    )
                    insertPokemonAbility(pokemonId, ability)
                    emit(Resource.success(ability))
                } else {
                    emit(
                        Resource.error(
                            pokemonAbilityRequest.message ?: "Data is empty",
                            null,
                            pokemonAbilityRequest.code
                        )
                    )
                }
            }
            Status.ERROR -> emit(
                Resource.error(
                    pokemonAbilityRequest.message ?: "General error",
                    null, pokemonAbilityRequest.code
                )
            )
            Status.LOADING -> emit(Resource.loading(null))
        }

    }

    private suspend fun insertPokemonAbility(remotePokemonId: Int, pokemonAbility: PokemonAbility) {
        withContext(Dispatchers.IO) {
            pokemonAbilityRepository.insertPokemonAbility(pokemonAbility)
            pokemonAbilityRepository.insertPokemonAbilityJoin(
                PokemonAbilityJoin(
                    remotePokemonId,
                    pokemonAbility.id
                )
            )
        }
    }

    fun setAbilityId(abilityId: Int) {
        this.abilityId.value = abilityId
        savedStateHandle.set(PokemonAbilityViewModel.abilityId, abilityId)
    }

    fun retry() {
        this.abilityId.value = this.abilityId.value
    }

    private fun getAbilityIdSavedState(): MutableLiveData<Int> {
        val id = savedStateHandle.get<Int>(PokemonAbilityViewModel.abilityId) ?: -1
        return MutableLiveData(id)
    }

    companion object {
        private const val abilityId: String = "pokemonId"
    }

}

