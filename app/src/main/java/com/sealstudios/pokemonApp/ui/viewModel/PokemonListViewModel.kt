package com.sealstudios.pokemonApp.ui.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonListViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var search: MutableLiveData<String> = getSearchState()
    val filters: MutableLiveData<MutableSet<String>> = getCurrentFiltersState()
    val searchPokemon: LiveData<List<PokemonWithTypesAndSpeciesForList>?>

    init {

        val combinedValues =
            MediatorLiveData<Pair<String?, MutableSet<String>?>?>().apply {
                addSource(search) {
                    Log.d("MAIN", "add search")
                    value = Pair(it, filters.value)
                }
                addSource(filters) {
                    Log.d("MAIN", "add filters")
                    value = Pair(search.value, it)
                }
            }

        searchPokemon = combinedValues.switchMap {
            Log.d("MAIN", "combinedValues.switchMap")
            liveData {
                emit(null)
                val search = it?.first ?: return@liveData
                val filters = it.second ?: return@liveData
                withContext(Dispatchers.IO) {
                    if (filters.isEmpty()) {
                        emitSource(searchPokemon(search))
                    } else {
                        emitSource(searchAndFilterPokemon(search, filters.toList()))
                    }
                }
            }
        }

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

    fun setSearch(search: String) {
        Log.d("MAIN", "set search")
        this.search.value = search
        savedStateHandle.set(searchKey, search)
    }

    fun refresh() {
        this.search.value = this.search.value
        savedStateHandle.set(searchKey, this.search.value)
    }

    private fun getSearchState(): MutableLiveData<String> {
        val searchString = savedStateHandle.get<String>(searchKey) ?: ""
        return MutableLiveData("%$searchString%")
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

    private fun getCurrentFiltersState(): MutableLiveData<MutableSet<String>> {
        val filters = savedStateHandle.get<MutableSet<String>>(filtersKey)
            ?: mutableSetOf()
        return MutableLiveData(filters)
    }

    companion object {
        private const val searchKey: String = "search"
        private const val filtersKey: String = "filters"
    }
}