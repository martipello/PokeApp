package com.sealstudios.pokemonApp.paging

import android.annotation.SuppressLint
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sealstudios.pokemonApp.api.`object`.PokemonGraphQlPokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.util.extensions.toLowerCase
import io.github.wax911.library.model.request.QueryContainerBuilder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@ExperimentalPagingApi
@FlowPreview
class PokemonPagingViewModel @ViewModelInject constructor(
        private val repository: PokemonPagingRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchState: MutableLiveData<String> = searchState()
    val selectedFilters: MutableLiveData<MutableSet<String>> = filtersState()
    val searchPokemon: Flow<PagingData<PokemonGraphQL>>

    init {
        searchPokemon = flow {
            combine(searchState.asFlow(), selectedFilters.asFlow()) { search, filters ->
                if (filters.isEmpty()) {
                    searchAndFilterPokemon(search, PokemonType.getAllPokemonTypes().map { it.name.toLowerCase() })
                } else {
                    searchAndFilterPokemon(search, filters.toList())
                }
            }
        }

        search("")
        clearFilters()

    }

    fun refresh() {
        savedStateHandle.set(searchKey, this.searchState.value)
    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemon(
            search: String,
            filters: List<String>
    ): Flow<PagingData<PokemonGraphQL>> {
        return repository.getPokemonGraphQL(
                PagingConfig(pageSize = 50, enablePlaceholders = true),
            QueryContainerBuilder().putVariables(
                mapOf(
                    "name" to search,
                    "type_names" to filters.map { filter -> filter.toLowerCase() },
                )
            )).cachedIn(viewModelScope)
    }

    fun addFilter(filter: String) {
        selectedFilters.value?.let {
            it.add(filter.toLowerCase())
            filter(it)
        }
    }

    fun removeFilter(filter: String) {
        selectedFilters.value?.let {
            it.remove(filter.toLowerCase())
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