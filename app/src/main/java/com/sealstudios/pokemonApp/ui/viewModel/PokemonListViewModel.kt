package com.sealstudios.pokemonApp.ui.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.repository.PokemonRepository

class PokemonListViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchPokemon: LiveData<List<PokemonWithTypesAndSpecies>>
    var search: MutableLiveData<String> = getSearchState()
    val filters: MutableLiveData<MutableSet<String>> = getCurrentFiltersState()

    val isFiltersLayoutExpanded: MutableLiveData<Boolean> = getFiltersLayoutExpanded()

    init {
        val combinedValues =
            MediatorLiveData<Pair<String?, MutableSet<String>?>?>().apply {
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
                searchAndFilterPokemon()
            } else null
        }
    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemon(): LiveData<List<PokemonWithTypesAndSpecies>> {
        return Transformations.switchMap(search) { search ->
            val allPokemon = repository.searchPokemonWithTypesAndSpecies(search)
            Transformations.switchMap(filters) { filters ->
                val pokemon = when {
                    filters.isNullOrEmpty() -> allPokemon
                    else -> {
                        Transformations.switchMap(allPokemon) { pokemonList ->
                            val filteredList = pokemonList.filter { pokemon ->
                                pokemon.matches = 0
                                val filter = filterTypes(pokemon, filters)
                                filter
                            }
                            maybeSortList(filters, filteredList)
                        }
                    }
                }
                pokemon
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filterTypes(
        pokemon: PokemonWithTypesAndSpecies,
        filters: MutableSet<String>
    ): Boolean {
        var match = false
        for (filter in filters) {
            for (type in pokemon.types) {
                if (type.name.toLowerCase() == filter.toLowerCase()) {
                    val matches = pokemon.matches?.plus(1)
                    pokemon.apply {
                        this.matches = matches
                    }
                    match = true
                }
            }
        }
        return match
    }

    private fun maybeSortList(
        filters: MutableSet<String>,
        filteredList: List<PokemonWithTypesAndSpecies>
    ): MutableLiveData<List<PokemonWithTypesAndSpecies>> {
        return if (filters.size > 1)
            MutableLiveData(filteredList.sortedByDescending {
                Log.d("VM", "SORTING ${it.pokemon.name} ${it.matches}")
                it.matches
            })
        else MutableLiveData(filteredList)
    }

    fun setSearch(search: String) {
        this.search.value = search
        savedStateHandle.set(searchKey, search)
    }

    fun addFilter(filter: String) {
        filters.value?.let {
            it.add(filter)
            this.filters.value = it
        }
    }

    fun removeFilter(filter: String) {
        filters.value?.let {
            it.remove(filter)
            this.filters.value = it
        }
    }

    fun setFiltersLayoutExpanded(isFiltersLayoutExpanded: Boolean) {
        this.isFiltersLayoutExpanded.value = isFiltersLayoutExpanded
        savedStateHandle.set(isFiltersLayoutExpandedKey, isFiltersLayoutExpanded)
    }

    private fun getSearchState(): MutableLiveData<String> {
        val searchString = savedStateHandle.get<String>(searchKey) ?: ""
        return MutableLiveData("%$searchString%")
    }

    private fun getCurrentFiltersState(): MutableLiveData<MutableSet<String>> {
        val filters = savedStateHandle.get<MutableSet<String>>(filtersKey)
            ?: mutableSetOf()
        return MutableLiveData(filters)
    }

    private fun getFiltersLayoutExpanded(): MutableLiveData<Boolean> {
        val isExpanded = savedStateHandle.get<Boolean>(isFiltersLayoutExpandedKey) ?: false
        return MutableLiveData(isExpanded)
    }

    companion object {
        private const val searchKey: String = "search"
        private const val filtersKey: String = "filters"
        private const val isFiltersLayoutExpandedKey: String = "filtersExpanded"
    }
}