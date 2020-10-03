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
                                if (filters.size > 1) {
                                    filterMultipleTypes(pokemon, filters)
                                } else {
                                    filterSingleTypes(pokemon, filters)
                                }
                            }
                            maybeSortList(filters, filteredList)
                        }
                    }
                }
                pokemon
            }
        }
    }

    private fun maybeSortList(
        filters: MutableSet<String>,
        filteredList: List<PokemonWithTypesAndSpecies>
    ): MutableLiveData<List<PokemonWithTypesAndSpecies>> {
        return if (filters.size > 1)
            MutableLiveData(filteredList.sortedByDescending { it.types.size })
        else MutableLiveData(filteredList)
    }

    @SuppressLint("DefaultLocale")
    private fun filterSingleTypes(
        pokemon: PokemonWithTypesAndSpecies,
        filters: MutableSet<String>
    ): Boolean {
        return pokemon.types.any { type ->
            Log.d("VM", type.name)
            filters.any { filter ->
                Log.d("VM", filter)
                type.name.toLowerCase() == filter.toLowerCase()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filterMultipleTypes(
        pokemon: PokemonWithTypesAndSpecies,
        filters: MutableSet<String>
    ): Boolean {
        return pokemon.types.all { type ->
            filters.any { filter ->
                type.name.toLowerCase() == filter.toLowerCase()
            }
        }
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
            it.removeAll {
                filters.value?.contains(filter) ?: false
            }
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