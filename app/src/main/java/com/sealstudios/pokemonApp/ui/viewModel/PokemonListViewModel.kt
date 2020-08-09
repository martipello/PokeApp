package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon


class PokemonListViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchPokemon: LiveData<List<dbPokemon>>
    private var search: MutableLiveData<String> = getCurrentSearchState()

    init {
        searchPokemon = Transformations.switchMap(search) { repository.searchPokemon(it) }
    }

    fun saveCurrentSearchState(search: String?) {
        savedStateHandle.set(searchKey, search)
    }

    private fun getCurrentSearchState(): MutableLiveData<String> {
        val searchString = savedStateHandle.get<String>(searchKey) ?: ""
        return MutableLiveData("%$searchString%")
    }

    fun setSearch(search: String) {
        this.search.value = search
    }

    companion object {

        private const val searchKey: String = "search"
    }
}