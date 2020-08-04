package com.sealstudios.pokemonApp.ui.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import com.sealstudios.pokemonApp.data.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokemonViewModel @ViewModelInject constructor(
        private val repository: PokemonRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val searchPokemon: LiveData<List<Pokemon>>
    private var search: MutableLiveData<String> = MutableLiveData("%%")

    init {
        searchPokemon = Transformations.switchMap(search) { repository.searchPokemon(it) }
    }

    fun setSearch(search: String) {
        this.search.value = search
    }

    fun insert(pokemon: Pokemon) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPokemon(pokemon)
    }
}