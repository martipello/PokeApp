package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.api.`object`.PokemonListResponse
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonViewModel @ViewModelInject constructor(
    private val remotePokemonRepository: RemotePokemonRepository,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    var allPokemonResponse: LiveData<Resource<PokemonListResponse>> = liveData {
        emit(Resource.loading(null))
        emit(getAllPokemonResponse())
    }

    private suspend fun getAllPokemonResponse(): Resource<PokemonListResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext remotePokemonRepository.getAllPokemonResponse()
        }
    }

    private suspend fun insertPokemon(pokemon: List<Pokemon>) = withContext(Dispatchers.IO) {
        pokemonRepository.insertPokemon(pokemon)
    }

    suspend fun saveAllPokemon(pokemonListResponseData: List<NamedApiResource>) =
        withContext(Dispatchers.IO) {
            insertPokemon(pokemonListResponseData.map {
                val id = Pokemon.getPokemonIdFromUrl(it.url)
                Pokemon(
                    id = id,
                    name = it.name,
                    image = Pokemon.highResPokemonUrl(id),
                    height = 0,
                    weight = 0,
                    move_ids = listOf(),
                    versionsLearnt = listOf(),
                    learnMethods = listOf(),
                    levelsLearnedAt = listOf(),
                    sprite = "",
                )
            })
        }

}
