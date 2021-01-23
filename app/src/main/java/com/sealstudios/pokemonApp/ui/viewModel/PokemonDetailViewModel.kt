package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.api.`object`.PokemonMoveResponse
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.mapDbPokemonFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonMove.Companion.getPokemonMoveIdFromUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.mapDbPokemonTypesFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin.Companion.mapTypeJoinsFromPokemonResponse
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
    private val pokemonSpeciesRepository: PokemonSpeciesRepository,
    private val pokemonMoveMetaDataRepository: PokemonMoveMetaDataRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var dominantAndLightVibrantColors: MutableLiveData<Pair<Int, Int>> = getViewColors()
    var revealAnimationExpanded: MutableLiveData<Boolean> = getRevealAnimationExpandedState()

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val pokemonDetail: LiveData<Resource<PokemonWithTypes>> = pokemonDetails()
    val pokemonSpecies: LiveData<Resource<PokemonSpecies>> = pokemonSpecies()

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
                        insertPokemonMoveMetaData(pokemonRequestData.moves, pokemon.id)
                    }
                    emit(
                        Resource.success(
                            PokemonWithTypes(
                                pokemon = pokemon,
                                types = mapDbPokemonTypesFromPokemonResponse(pokemonRequestData)
                            )
                        )
                    )
                } else {
                    emit(Resource.error(pokemonRequest.message ?: "Data is empty", null,pokemonRequest.code))
                }
            }
            Status.ERROR -> emit(Resource.error(pokemonRequest.message ?: "General error", null, pokemonRequest.code))
            Status.LOADING -> emit(Resource.loading(null))
        }
    }

    private fun fetchPokemonSpecies(pokemonId: Int) = liveData(Dispatchers.IO) {
        val pokemonSpeciesRequest = remotePokemonRepository.speciesForId(pokemonId)

        when (pokemonSpeciesRequest.status) {
            Status.SUCCESS -> {
                if (pokemonSpeciesRequest.data != null) {
                    val species = PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(
                        pokemonSpeciesRequest.data
                    )
                    insertPokemonSpecies(pokemonId, species)
                    emit(Resource.success(species))
                } else {
                    emit(Resource.error(pokemonSpeciesRequest.message ?: "Data is empty", null, pokemonSpeciesRequest.code))
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

    private suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        withContext(Dispatchers.IO) {
            pokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
            pokemonSpeciesRepository.insertPokemonSpeciesJoin(
                PokemonSpeciesJoin(
                    remotePokemonId,
                    pokemonSpecies.id
                )
            )
        }
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {
            pokemonTypeRepository.insertPokemonTypes(
                mapDbPokemonTypesFromPokemonResponse(
                    remotePokemon
                )
            )
            pokemonTypeRepository.insertPokemonTypeJoins(
                mapTypeJoinsFromPokemonResponse(
                    remotePokemon
                )
            )
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
                    moveResponse.version_group_details)
                Log.d(TAG, "INSERT METADATA $moveMetaData")
                pokemonMoveMetaDataRepository.insertMoveMetaData(moveMetaData)
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

