package com.sealstudios.pokemonApp.ui.viewModel

import android.annotation.SuppressLint
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PagedPokemonViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var search: MutableLiveData<String> = getSearchState()
    val filters: MutableLiveData<MutableSet<String>> = getCurrentFiltersState()
    val searchPokemon: LiveData<PagingData<PokemonWithTypesAndSpeciesForList>>

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

        searchPokemon = combinedValues.switchMap {
            liveData {
                val search = it?.first ?: return@liveData
                val filters = it.second ?: return@liveData
                withContext(Dispatchers.IO) {
                    emitSource(searchAndFilterPokemonPager(search, filters.toList()))
                }
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemonPager(
        search: String,
        filters: List<String>
    ): LiveData<PagingData<PokemonWithTypesAndSpeciesForList>> {
        return Pager(
            config = PagingConfig(
                pageSize = 40,
                enablePlaceholders = true,
                maxSize = 120
            )
        ) {
            if (filters.isEmpty()) {
                searchPokemonForPaging(search)
            } else {
                searchAndFilterPokemonForPaging(search, filters)
            }
        }.liveData.cachedIn(viewModelScope)
    }

    @SuppressLint("DefaultLocale")
    private fun searchPokemonForPaging(search: String): PagingSource<Int, PokemonWithTypesAndSpeciesForList> {
        return repository.searchPokemonWithTypesAndSpeciesForPaging(search)
    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemonForPaging(
        search: String,
        filters: List<String>
    ): PagingSource<Int, PokemonWithTypesAndSpeciesForList> {
        return repository.searchAndFilterPokemonWithTypesAndSpeciesForPaging(
            search,
            filters.map { filter -> filter.toLowerCase() })
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