package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.mapDbPokemonFromPokemonResponse
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
    private val pokemonTypeJoinRepository: PokemonTypeJoinRepository,
    private val pokemonSpeciesRepository: PokemonSpeciesRepository,
    private val pokemonSpeciesJoinRepository: PokemonSpeciesJoinRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var pokemon: LiveData<PokemonWithTypesAndSpecies> = MutableLiveData()
    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()
    var dominantAndLightVibrantColors: MutableLiveData<Pair<Int, Int>> = getViewColors()
    var revealAnimationExpanded: MutableLiveData<Boolean> = getRevealAnimationExpandedState()

    init {
        viewModelScope.launch {
            pokemon =
                Transformations.distinctUntilChanged(Transformations.switchMap(pokemonId) { id ->
                    val pokemonLiveData = repository.getSinglePokemonById(id)
                    Transformations.switchMap(pokemonLiveData) { pokemon ->
                        pokemon?.let {
                            if (it.species?.pokedexEntry == null || it.types.isEmpty()) {
                                viewModelScope.launch {
                                    fetchRemotePokemon(id)
                                }
                            }
                            MutableLiveData(pokemon)
                        }
                    }
                })
        }
    }

    private suspend fun fetchRemotePokemon(id: Int) = withContext(Dispatchers.IO) {
        fetchPokemonForId(id)
        fetchSpeciesForId(id)
    }

    private suspend fun fetchPokemonForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonRequest = remotePokemonRepository.pokemonForId(remotePokemonId)
            when(pokemonRequest.status){
                Status.SUCCESS -> {
                    pokemonRequest.data?.let {
                        repository.insertPokemon(mapDbPokemonFromPokemonResponse(it))
                        insertPokemonTypes(it)
                    }
                }
                Status.ERROR -> Log.d(TAG,"fetchPokemonForId ERROR")
                Status.LOADING -> Log.d(TAG,"fetchPokemonForId LOADING")
            }
        }
    }

    private suspend fun fetchSpeciesForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonSpeciesRequest = remotePokemonRepository.speciesForId(remotePokemonId)
            when(pokemonSpeciesRequest.status){
                Status.SUCCESS -> {
                    pokemonSpeciesRequest.data?.let {
                        insertPokemonSpecies(
                            remotePokemonId,
                            PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(it)
                        )
                    }
                }
                Status.ERROR -> Log.d(TAG,"fetchSpeciesForId ERROR")
                Status.LOADING -> Log.d(TAG,"fetchSpeciesForId LOADING")
            }
        }
    }

    private suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        withContext(Dispatchers.IO) {
            pokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
            pokemonSpeciesJoinRepository.insertPokemonSpeciesJoin(
                PokemonSpeciesJoin(
                    remotePokemonId,
                    pokemonSpecies.id
                )
            )
        }
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon
    ) {
        withContext(Dispatchers.IO) {
            pokemonTypeRepository.insertPokemonTypes(PokemonType.mapDbPokemonTypesFromPokemonResponse(remotePokemon))
            pokemonTypeJoinRepository.insertPokemonTypeJoins(PokemonTypesJoin.mapTypeJoinsFromPokemonResponse(remotePokemon))
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

