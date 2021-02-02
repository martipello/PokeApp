package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.mapDbPokemonFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility.Companion.getPokemonAbilityIdFromUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonMove.Companion.getPokemonMoveIdFromUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.mapDbPokemonTypesFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin.Companion.mapTypeJoinsFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.isDefault
import com.sealstudios.pokemonApp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val <A, B> Pair<A, B>.dominantColor: A get() = this.first
val <A, B> Pair<A, B>.lightVibrantColor: B get() = this.second

class PokemonDetailViewModel @ViewModelInject constructor(
    private val repository: PokemonWithTypesAndSpeciesRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
    private val pokemonTypeRepository: PokemonTypeRepository,
    private val pokemonWithTypesRepository: PokemonWithTypesRepository,
    private val pokemonMoveMetaDataRepository: PokemonMoveMetaDataRepository,
    private val pokemonAbilityMetaDataRepository: PokemonAbilityMetaDataRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var dominantAndLightVibrantColors: MutableLiveData<Pair<Int, Int>> = getViewColors()
    var revealAnimationExpanded: MutableLiveData<Boolean> = getRevealAnimationExpandedState()

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val pokemonDetail: LiveData<Resource<PokemonWithTypes>> = pokemonDetails()

    private fun pokemonDetails() = pokemonId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithTypes = pokemonWithTypesRepository.getSinglePokemonWithTypesByIdAsync(id)
            if (pokemonWithTypes.pokemon.isDefault()) {
                emitSource(fetchPokemonDetails(pokemonWithTypes))
            } else {
                emit(
                    Resource.success(
                        PokemonWithTypes(
                            pokemon = pokemonWithTypes.pokemon,
                            types = pokemonWithTypes.types
                        )
                    )
                )
            }
        }
    }

    private fun fetchPokemonDetails(pokemonWithTypes: PokemonWithTypes) = liveData {
        val pokemonRequest = remotePokemonRepository.pokemonForId(pokemonWithTypes.pokemon.id)
        when (pokemonRequest.status) {
            Status.SUCCESS -> {
                if (pokemonRequest.data != null) {
                    val pokemonRequestData = pokemonRequest.data
                    val pokemon = mapDbPokemonFromPokemonResponse(pokemonRequestData)
                    viewModelScope.launch {
                        repository.updatePokemon(pokemon)
                        insertPokemonTypes(pokemonRequestData)
                        pokemonRequestData.moves?.let { insertPokemonMoveMetaData(it, pokemon.id) }
                        pokemonRequestData.abilities?.let { insertPokemonAbilityMetaData(it, pokemon.id) }
                    }
                    emit(
                        Resource.success(
                            mapDbPokemonTypesFromPokemonResponse(pokemonRequestData)?.let {
                                PokemonWithTypes(
                                    pokemon = pokemon,
                                    types = it
                                )
                            }
                        )
                    )
                } else {
                    emit(Resource.error(pokemonRequest.message ?: "Data is empty", null, pokemonRequest.code))
                }
            }
            Status.ERROR -> emit(Resource.error(pokemonRequest.message ?: "General error", null, pokemonRequest.code))
            Status.LOADING -> emit(Resource.loading(null))
        }
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {
            mapDbPokemonTypesFromPokemonResponse(
                remotePokemon
            )?.let {
                pokemonTypeRepository.insertPokemonTypes(
                    it
                )
            }
            mapTypeJoinsFromPokemonResponse(
                remotePokemon
            )?.let {
                pokemonTypeRepository.insertPokemonTypeJoins(
                    it
                )
            }
        }
    }

    private suspend fun insertPokemonMoveMetaData(moves: List<PokemonMoveResponse>, pokemonId: Int) {
        for (moveResponse in moves) {
            val moveId = getPokemonMoveIdFromUrl(moveResponse.move.url)
            withContext(Dispatchers.IO) {
                val moveMetaData = PokemonMoveMetaData.mapRemotePokemonToMoveMetaData(
                    moveId,
                    pokemonId,
                    moveResponse.move.name,
                    moveResponse.version_group_details
                )
                pokemonMoveMetaDataRepository.insertMoveMetaData(moveMetaData)
            }
        }
    }

    private suspend fun insertPokemonAbilityMetaData(abilities: List<PokemonAbility>, pokemonId: Int) {
        for (abilityResponse in abilities) {
            val abilityId = getPokemonAbilityIdFromUrl(abilityResponse.ability.url)
            withContext(Dispatchers.IO) {
                val abilityMetaData = PokemonAbilityMetaData.mapRemotePokemonToAbilityMetaData(
                    abilityId,
                    pokemonId,
                    abilityResponse.ability.name,
                    abilityResponse.isHidden
                )
                pokemonAbilityMetaDataRepository.insertAbilityMetaData(abilityMetaData)
            }
        }
    }

    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
        savedStateHandle.set(PokemonDetailViewModel.pokemonId, pokemonId)
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        val id = savedStateHandle.get<Int>(PokemonDetailViewModel.pokemonId) ?: -1
        return MutableLiveData(id)
    }

    fun setRevealAnimationExpandedState(hasExpanded: Boolean) {
        revealAnimationExpanded.value = hasExpanded
        savedStateHandle.set(hasExpandedKey, hasExpanded)
    }

    private fun getRevealAnimationExpandedState(): MutableLiveData<Boolean> {
        val hasExpanded = savedStateHandle.get<Boolean>(hasExpandedKey) ?: false
        return MutableLiveData(hasExpanded)
    }

    private fun getViewColors(): MutableLiveData<Pair<Int, Int>> {
        val dominantColor = savedStateHandle.get<Int>(dominantColorKey) ?: 0
        val lightVibrantColor = savedStateHandle.get<Int>(lightVibrantColorKey) ?: 0
        return MutableLiveData(dominantColor to lightVibrantColor)
    }

    fun setViewColors(dominantColor: Int, lightVibrantColor: Int) {
        dominantAndLightVibrantColors.value = dominantColor to lightVibrantColor
        savedStateHandle.set(lightVibrantColorKey, lightVibrantColor)
        savedStateHandle.set(dominantColorKey, dominantColor)
    }

    companion object {
        private const val pokemonId: String = "pokemonId"
        private const val hasExpandedKey: String = "hasExpanded"
        private const val lightVibrantColorKey: String = "lightVibrantColor"
        private const val dominantColorKey: String = "dominantColor"
        private const val TAG: String = "PDVM"
    }

}

