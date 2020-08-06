package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon

class PokemonListViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val searchKey: String = "search"
    private val tag: String = "POKE_LIST_VIEW_MODEL"
    val searchPokemon: LiveData<List<dbPokemon>>
    private var search: MutableLiveData<String> = MutableLiveData("%%")

    init {
        searchPokemon = Transformations.switchMap(search) { repository.searchPokemon(it) }
    }

    fun saveCurrentSearch(search: String) {
        savedStateHandle.set(searchKey, search)
    }

    fun setCurrentSearch() {
        setSearch(savedStateHandle.get(searchKey) ?: "")
    }

    fun getRemotePokemon() {
        viewModelScope.launch {
            val pokemonList = arrayListOf<dbPokemon>()
            val pokemonResponse = repository.getRemotePokemon().body()
            pokemonResponse?.results?.let { pokemonResponseResult ->
                for (pokemon in pokemonResponseResult) {
                    pokemon?.url?.let { pokemonUrl ->
                        try {
                            val pokemonId = getPokemonIdFromUrl(pokemonUrl)
                            when {
                                pokemonId >= 0 -> {
                                    val pokemonRequest =
                                        async { repository.getRemotePokemonById(pokemonId) }
                                    pokemonRequest.await().body()?.let { pokemon ->
                                        val dbPokemon = buildDbPokemonFromRemotePokemon(pokemon)
                                        pokemonList.add(dbPokemon)
                                    }
                                }
                                else -> {
                                }
                            }
                        } catch (exception: Exception) {
                            exception.message?.let {
                                Log.e(tag, it)
                            }
                        }
                    }
                }
                repository.insertPokemon(pokemonList)
            }
        }
    }

    private fun buildDbPokemonFromRemotePokemon(pokemon: apiPokemon): dbPokemon {
        return dbPokemon(
            id = pokemon.id,
            name = pokemon.name,
            url = "https://pokeres.bastionbot.org/images/pokemon/${pokemon.id}.png",
            weight = 0,
            height = 0
        )
    }

    private fun getPokemonIdFromUrl(pokemonUrl: String): Int {
        val pokemonIndex = pokemonUrl.split("/".toRegex()).toTypedArray()
        return if (pokemonIndex.size >= 2) {
            pokemonIndex[pokemonIndex.size - 2].toInt()
        } else {
            -1
        }
    }

    fun setSearch(search: String) {
        this.search.value = search
    }
}