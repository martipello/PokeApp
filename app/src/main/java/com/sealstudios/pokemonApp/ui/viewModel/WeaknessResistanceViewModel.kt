package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes
import com.sealstudios.pokemonApp.repository.TypeRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WeaknessResistanceViewModel @ViewModelInject constructor(
        private val typeRepository: TypeRepository,
        private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = MutableLiveData()

    val pokemonWeaknessAndResistance: LiveData<Resource<PokemonWithTypes>> = pokemonId.switchMap { pokemonId ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithTypes = typeRepository.getPokemonById(pokemonId)
            if (pokemonWithTypes.types.any { it.doubleDamageFrom.isEmpty() }) {
                pokemonWithTypes.types.map { pokemonType ->
                    if (pokemonType.doubleDamageFrom.isEmpty()) {
                        emitSource(fetchPokemonType(pokemon = pokemonWithTypes.pokemon, typeId = pokemonType.id))
                    }
                }
            } else {
                emit(Resource.success(pokemonWithTypes))
            }
        }
    }

    private suspend fun fetchPokemonType(pokemon: Pokemon, typeId: Int) =
            liveData(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    val fetchType = async {
                        fetchAndSavePokemonType(pokemon, typeId)
                    }
                    fetchType.await()
                    emit(
                            Resource.success(
                                    typeRepository.getPokemonById(pokemon.id)
                            )
                    )
                }
            }

    private suspend fun fetchAndSavePokemonType(
            pokemon: Pokemon,
            typeId: Int
    ) {
        withContext(Dispatchers.IO) {
            val typeRequest = remotePokemonRepository.pokemonTypeForId(typeId)
            when (typeRequest.status) {
                Status.SUCCESS -> {
                    typeRequest.data?.let {
                        typeRepository.updateType(it, pokemon.id)
                    }
                }
                else -> {
                }
            }
        }
    }


    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

}

