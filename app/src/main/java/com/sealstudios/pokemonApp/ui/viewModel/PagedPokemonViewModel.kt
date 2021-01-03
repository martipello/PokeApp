package com.sealstudios.pokemonApp.ui.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PagedPokemonViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
    private val pokemonSpeciesRepository: PokemonSpeciesRepository,
    private val pokemonTypeRepository: PokemonTypeRepository,
    private val pokemonTypeJoinRepository: PokemonTypeJoinRepository,
    private val pokemonSpeciesJoinRepository: PokemonSpeciesJoinRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var search: MutableLiveData<String> = getSearchState()
    val filters: MutableLiveData<MutableSet<String>> = getCurrentFiltersState()
    val searchPokemon: LiveData<PagingData<PokemonWithTypesAndSpeciesForList>>

    val isFiltersLayoutExpanded: MutableLiveData<Boolean> = getFiltersLayoutExpanded()

    init {

        //TODO when the underlining data updates it causes this to be called
        // but this is done on the UI thread
        // causing an error
        /*
            11480-11480/com.sealstudios.pokemonApp E/ROOM: Cannot run invalidation tracker. Is the db closed?
            java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
            at androidx.room.RoomDatabase.assertNotMainThread(RoomDatabase.java:341)
         */

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
                searchAndFilterPokemonPager(search, filters.toList())
            } else null
        }.distinctUntilChanged()
    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemonPager(search: String, filters: List<String>): LiveData<PagingData<PokemonWithTypesAndSpeciesForList>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 60
            )
        ) {
            if (filters.isEmpty()){
                searchPokemonForPaging(search)
            } else {
                searchAndFilterPokemonForPaging(search, filters)
            }
        }.liveData.cachedIn(viewModelScope)
    }

    suspend fun fetchRemotePokemon(id: Int) = withContext(Dispatchers.IO) {
        fetchPokemonForId(id)
        fetchSpeciesForId(id)
    }

    private suspend fun fetchPokemonForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonRequest =
                remotePokemonRepository.pokemonById(remotePokemonId)
            pokemonRequest.let { pokemonResponse ->
                if (pokemonResponse.isSuccessful) {
                    pokemonResponse.body()?.let { pokemon ->
                        repository.insertPokemon(Pokemon.mapDbPokemonFromPokemonResponse(pokemon))
                        insertPokemonTypes(pokemon)
                    }
                }
            }
        }
    }

    private suspend fun fetchSpeciesForId(
        remotePokemonId: Int
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonSpeciesRequest =
                remotePokemonRepository.speciesById(remotePokemonId)
            pokemonSpeciesRequest.let { pokemonSpeciesResponse ->
                if (pokemonSpeciesResponse.isSuccessful) {
                    pokemonSpeciesResponse.body()?.let { species ->
                        insertPokemonSpecies(
                            remotePokemonId,
                            PokemonSpecies.mapRemotePokemonSpeciesToDatabasePokemonSpecies(species)
                        )
                    }
                }
            }

        }
    }

    private suspend fun insertPokemonSpecies(remotePokemonId: Int, pokemonSpecies: PokemonSpecies) {
        withContext(Dispatchers.IO) {
            pokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
            pokemonSpeciesJoinRepository.insertPokemonSpeciesJoin(
                PokemonSpeciesJoin(
                    remotePokemonId,
                    pokemonSpecies.id
                )
            )
        }
    }

    private suspend fun insertPokemonTypes(
        remotePokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon
    ) {
        withContext(Dispatchers.IO) {
            pokemonTypeRepository.insertPokemonTypes(PokemonType.mapDbPokemonTypesFromPokemonResponse(remotePokemon))
            pokemonTypeJoinRepository.insertPokemonTypeJoins(PokemonTypesJoin.mapTypeJoinsFromPokemonResponse(remotePokemon))
        }
    }


    @SuppressLint("DefaultLocale")
    private fun getAllPokemonForPaging(): PagingSource<Int, PokemonWithTypesAndSpecies> {
        return repository.getAllPokemonWithTypesAndSpeciesForPaging()
    }

    @SuppressLint("DefaultLocale")
    private fun searchPokemonForPaging(search: String): PagingSource<Int, PokemonWithTypesAndSpeciesForList> {
        return repository.searchPokemonWithTypesAndSpeciesForPaging(search)
    }

    @SuppressLint("DefaultLocale")
    private fun searchAndFilterPokemonForPaging(search: String, filters: List<String>): PagingSource<Int, PokemonWithTypesAndSpeciesForList> {
        return repository.searchAndFilterPokemonWithTypesAndSpeciesForPaging(search, filters.map { filter -> filter.toLowerCase() })
    }

    @SuppressLint("DefaultLocale")
    private fun filterTypesForFlow(
        pokemon: PokemonWithTypesAndSpeciesForList,
        filters: MutableSet<String>
    ): Boolean {
        if (filters.isEmpty()){
            return true
        }
        return filters.any { filter ->
            Log.d("PPVM", "filter $filter")
            pokemon.types.any { type ->
                Log.d("PPVM", "type $type")
                filter.equals(type.name, ignoreCase = true)
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
                if (type.name.equals(filter, ignoreCase = true)) {
                    val matches = pokemon.matches.plus(1)
                    pokemon.apply {
                        this.matches = matches
                    }
                    match = true
                }
            }
        }
        return match
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