package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import kotlinx.coroutines.launch
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon

class PokemonDetailViewModel @ViewModelInject constructor(
    private val repository: PokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val localPokemon: LiveData<dbPokemon>
    private var search: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        localPokemon = Transformations.distinctUntilChanged(Transformations.switchMap(search) {
            repository.getSinglePokemonById(it)
        })
    }

    fun getRemotePokemon(dbPokemon: dbPokemon) {
        viewModelScope.launch {
            val pokemon = repository.getRemotePokemonById(dbPokemon.id).body()
            pokemon?.let {
                val pokemonToInsert = dbPokemon(
                    id = dbPokemon.id,
                    name = pokemon.name,
                    height = pokemon.height,
                    weight = pokemon.weight,
                    url = dbPokemon.url
                )
                repository.insertPokemon(pokemonToInsert)
            }
        }
    }

    fun setSearch(search: Int) {
        this.search.value = search
    }
}
