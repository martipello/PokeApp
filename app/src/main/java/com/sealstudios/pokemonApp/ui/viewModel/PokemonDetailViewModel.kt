package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PokemonDetailViewModel @ViewModelInject constructor(
        private val repository: PokemonRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tag: String = "POKE_VIEW_MODEL"
    val localPokemon: LiveData<Pokemon>
    private var search: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        localPokemon = Transformations.switchMap(search) { repository.getSinglePokemonById(it) }
    }

    private fun getRemotePokemon(id: Int) {
        viewModelScope.launch {
            val pokemon = repository.getRemotePokemonById(id).body()
            pokemon?.let {
                repository.insertPokemon(it)
            }
        }
    }

    fun setSearch(search: Int) {
        this.search.value = search
    }
}
