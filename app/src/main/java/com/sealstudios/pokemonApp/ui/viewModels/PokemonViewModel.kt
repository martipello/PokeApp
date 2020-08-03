package com.sealstudios.pokemonApp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import com.sealstudios.pokemonApp.objects.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PokemonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PokemonRepository
    val allPokemon: LiveData<List<Pokemon>>

    init {
        val pokemonDao = PokemonRoomDatabase.getDatabase(application, viewModelScope).pokemonDao()
        repository = PokemonRepository(pokemonDao)
        allPokemon = repository.allPokemon
    }

    fun insert(pokemon: Pokemon) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPokemon(pokemon)
    }
}