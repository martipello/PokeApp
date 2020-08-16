package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.repository.PokemonRepository

//TODO fix this not keeping the search state but does keep the list state
class PokemonListViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchPokemon: LiveData<List<PokemonWithTypes>>
    private var search: MutableLiveData<String> = getCurrentSearchState()
    private val filters: MutableLiveData<List<String>> = getCurrentFiltersState()

    init {
        val combinedValues = MediatorLiveData<Pair<String?, List<String>?>?>().apply {
            addSource(search) {
                value = Pair(it, filters.value)
            }
            addSource(filters) {
                value = Pair(search.value, it)
            }
        }

        searchPokemon = Transformations.switchMap(combinedValues) { pair ->
            val search = pair?.first
            val filters = pair?.second
            if (search != null && filters != null) {
                repository.searchPokemonWithTypeFilters(search, filters)
            } else null
        }
    }

    fun setSearch(search: String) {
        this.search.value = search
    }

    fun setFilters(filters: List<String>) {
        this.filters.value = filters
    }

    fun saveCurrentSearchState(search: String?) {
        savedStateHandle.set(searchKey, search)
    }

    private fun getCurrentSearchState(): MutableLiveData<String> {
        val searchString = savedStateHandle.get<String>(searchKey) ?: ""
        return MutableLiveData("%$searchString%")
    }

    private fun getCurrentFiltersState(): MutableLiveData<List<String>> {
        val filters = savedStateHandle.get<List<String>>(filtersKey) ?: emptyList()
        return MutableLiveData(filters)
    }

    fun saveCurrentFilterState(filters: List<String>) {
        this.filters.value = filters
    }

    companion object {
        private const val searchKey: String = "search"
        private const val filtersKey: String = "filters"
    }
}