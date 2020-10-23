package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.repository.PokemonWithTypesAndSpeciesRepository

val <A, B> Pair<A, B>.dominantColor: A get() = this.first
val <A, B> Pair<A, B>.lightVibrantColor: B get() = this.second

class PokemonDetailViewModel @ViewModelInject constructor(
    private val repository: PokemonWithTypesAndSpeciesRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pokemon: LiveData<PokemonWithTypesAndSpecies>
    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()
    var dominantAndLightVibrantColors: MutableLiveData<Pair<Int, Int>> = getViewColors()
    var revealAnimationExpanded: MutableLiveData<Boolean> = getRevealAnimationExpandedState()

    init {
        pokemon = Transformations.distinctUntilChanged(Transformations.switchMap(pokemonId) { id ->
            val pokemonLiveData = repository.getSinglePokemonById(id)
            Transformations.switchMap(pokemonLiveData) { pokemon ->
                pokemon?.let {
                    MutableLiveData(pokemon)
                }
            }
        })
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
    }

}

