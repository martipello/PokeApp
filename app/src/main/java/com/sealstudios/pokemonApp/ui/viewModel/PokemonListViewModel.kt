package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.initializePokemonTypeFilters

//TODO fix this not keeping the search state but does keep the list state
class PokemonListViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchPokemon: LiveData<List<PokemonWithTypes>>
    private var search: MutableLiveData<String> = getCurrentSearchState()
    val filters: MutableLiveData<MutableMap<String, Boolean>> = getCurrentFiltersState()

    init {
        val combinedValues =
            MediatorLiveData<Pair<String?, MutableMap<String, Boolean>?>?>().apply {
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
                checkSelections()
            } else null
        }
    }

    private fun checkSelections(): LiveData<List<PokemonWithTypes>> {
        val selections = filters.value?.filterValues { it }
        if (selections.isNullOrEmpty()) {
            return searchPokemon()
        }
        return searchAndFilterPokemon()
    }

    private fun searchPokemon(): LiveData<List<PokemonWithTypes>> {
        return Transformations.switchMap(search) { searchName ->
            repository.searchPokemon(searchName)
        }
    }

    private fun searchAndFilterPokemon(): LiveData<List<PokemonWithTypes>> {
        return Transformations.switchMap(search) { searchName ->
            val allPokemon = repository.searchPokemon(searchName)
            allPokemon.switchMap { pokemonList ->
                val filteredList = filters.value?.flatMap { filter ->
                    pokemonList.filter { pokemonWithTypes ->
                        pokemonWithTypes.types.any { pokemonType ->
                            pokemonType.name == filter.key.toLowerCase() && filter.value
                        }
                    }
                }
                MutableLiveData<List<PokemonWithTypes>>(
                    filteredList?.distinct()?.sortedBy { it.pokemon.id })
            }
        }
    }

    fun setSearch(search: String) {
        this.search.value = search
    }

    fun setFilter(key: String, value: Boolean) {
        val x = filters.value
        x!![key] = value
        setFilters(x)
    }

    fun setFilters(filters: MutableMap<String, Boolean>) {
        this.filters.value = filters
    }

    fun saveCurrentSearchState(search: String?) {
        savedStateHandle.set(searchKey, search)
    }

    private fun getCurrentSearchState(): MutableLiveData<String> {
        val searchString = savedStateHandle.get<String>(searchKey) ?: ""
        return MutableLiveData("%$searchString%")
    }

    private fun getCurrentFiltersState(): MutableLiveData<MutableMap<String, Boolean>> {
        val filters = savedStateHandle.get<MutableMap<String, Boolean>>(filtersKey)
            ?: initializePokemonTypeFilters()
        return MutableLiveData(filters)
    }

    fun saveCurrentFilterState(filters: MutableMap<String, Boolean>) {
        this.filters.value = filters
    }

    companion object {
        private const val searchKey: String = "search"
        private const val filtersKey: String = "filters"
    }
}