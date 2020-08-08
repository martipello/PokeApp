package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.mapRemotePokemonToDatabasePokemon
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import kotlinx.coroutines.CoroutineScope
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

    fun setSearch(search: Int) {
        this.search.value = search
    }

    companion object {
        fun getRemotePokemonDetail(
            scope: CoroutineScope,
            dbPokemon: dbPokemon,
            repository: PokemonRepository
        ) {
            scope.launch {
                val pokemon = repository.getRemotePokemonById(dbPokemon.id).body()
                pokemon?.let {
                    repository.insertPokemon(mapRemotePokemonToDatabasePokemon(dbPokemon, pokemon))
                }
            }
        }
    }
}

