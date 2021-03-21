package com.sealstudios.pokemonApp.ui.viewModel

import android.annotation.SuppressLint
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.repository.PokemonRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
class PokemonListViewModel @ViewModelInject constructor(
        private val repository: PokemonRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchState: MutableLiveData<String> = searchState()
    val selectedFilters: MutableLiveData<MutableSet<String>> = filtersState()
    val searchPokemon: Flow<List<PokemonWithTypesAndSpeciesForList>?>

    init {

        searchPokemon = flow {
            emitAll(combine(searchState.asFlow(), selectedFilters.asFlow()) { search, filters ->
                if (filters.isEmpty()) {
                    searchPokemon(search).asFlow()
                } else {
                    searchAndFilterPokemon(search, filters.toList()).asFlow()
                }
            }.flattenMerge())
        }

        search("")
        clearFilters()

    }

    @SuppressLint("DefaultLocale")
    private fun searchPokemon(search: String): LiveData<List<PokemonWithTypesAndSpeciesForList>?> {
        return repository.searchPokemonWithTypesAndSpecies(search)
    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemon(
            search: String,
            filters: List<String>
    ): LiveData<List<PokemonWithTypesAndSpeciesForList>?> {
        return repository.searchAndFilterPokemonWithTypesAndSpecies(
                search,
                filters.map { filter -> filter.toLowerCase() })
    }

    fun addFilter(filter: String) {
        selectedFilters.value?.let {
            it.add(filter)
            filter(it)
        }
    }

    fun removeFilter(filter: String) {
        selectedFilters.value?.let {
            it.remove(filter)
            filter(it)
        }
    }

    private fun filter(filters: MutableSet<String>) {
        savedStateHandle.set(filtersKey, filters)
    }

    fun search(search: String) {
        savedStateHandle.set(searchKey, "%$search%")
    }

    private fun searchState(): MutableLiveData<String> {
        return savedStateHandle.getLiveData(searchKey)
    }

    private fun filtersState(): MutableLiveData<MutableSet<String>> {
        return savedStateHandle.getLiveData(filtersKey)
    }

    fun clearFilters() {
        savedStateHandle.set(filtersKey, mutableSetOf<String>())
    }

    companion object {
        private const val searchKey: String = "search"
        private const val filtersKey: String = "filters"
    }
}