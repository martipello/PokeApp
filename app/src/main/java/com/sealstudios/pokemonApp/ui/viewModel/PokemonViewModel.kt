package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PokemonViewModel @ViewModelInject constructor(
        private val repository: PokemonRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tag: String = "POKE_VIEW_MODEL"
    val searchPokemon: LiveData<List<Pokemon>>
    private var search: MutableLiveData<String> = MutableLiveData("%%")

    init {
        searchPokemon = Transformations.switchMap(search) { repository.searchPokemon(it) }
        getRemotePokemon()
    }

    private fun getRemotePokemon() {
        viewModelScope.launch {
            val pokemonList = arrayListOf<Pokemon>()
            val pokemonResponse = repository.getRemotePokemon().body()
            pokemonResponse?.results?.let { pokemonResponseResult ->
                for (pokemon in pokemonResponseResult) {
                    pokemon?.url?.let { pokemonUrl ->
                        try {
                            val pokemonId = getPokemonIdFromUrl(pokemonUrl)
                            when {
                                pokemonId >= 0 -> {
                                    val pokemonRequest = async { repository.getRemotePokemonById(pokemonId) }
                                    pokemonRequest.await().body()?.let { pokemon ->
                                        pokemon.id = pokemonId
                                        pokemon.url = "https://pokeres.bastionbot.org/images/pokemon/$pokemonId.png"
                                        pokemonList.add(pokemon)
                                    }
                                }
                                else -> {}
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

    private fun getPokemonIdFromUrl(pokemonUrl: String): Int {
        val pokemonIndex = pokemonUrl.split("/".toRegex()).toTypedArray()
        return if (pokemonIndex.size >= 2){
            pokemonIndex[pokemonIndex.size - 2].toInt()
        } else {
            -1
        }
    }

    fun setSearch(search: String) {
        this.search.value = search
    }

    fun insert(pokemon: Pokemon) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPokemon(pokemon)
    }
}