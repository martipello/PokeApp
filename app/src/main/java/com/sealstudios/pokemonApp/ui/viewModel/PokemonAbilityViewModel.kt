package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.PokemonAbilityMetaDataRepository
import com.sealstudios.pokemonApp.repository.PokemonAbilityRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.*

class PokemonAbilityViewModel @ViewModelInject constructor(
    private val pokemonAbilityRepository: PokemonAbilityRepository,
    private val pokemonAbilityMetaDataRepository: PokemonAbilityMetaDataRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemon: MutableLiveData<Pokemon> = MutableLiveData()

    // Doesn't handle errors as there isn't a way to emit them from the for loop in fetchPokemonAbilities
    // and meta data comes from the pokemon and not the move meaning we would double the calls to the API

    val pokemonAbilities: LiveData<Resource<PokemonWithAbilitiesAndMetaData>> = pokemon.switchMap { pokemon ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithAbilitiesAndMetaData =
                pokemonAbilityMetaDataRepository.getPokemonWithAbilitiesAndMetaDataByIdAsync(pokemon.id)
            if (pokemonWithAbilitiesAndMetaData.abilities.size != pokemon.abilityIds.size) {
                emitSource(fetchPokemonAbilities(pokemon, pokemonWithAbilitiesAndMetaData.abilities))
            } else {
                emit(Resource.success(pokemonWithAbilitiesAndMetaData))
            }

            if (pokemonWithAbilitiesAndMetaData.abilities.size > pokemonWithAbilitiesAndMetaData.pokemonAbilityMetaData.size) {
                joinMetaDataToAbility(pokemonWithAbilitiesAndMetaData, pokemon)
            }
        }
    }

    private suspend fun fetchPokemonAbilities(pokemon: Pokemon, abilities: List<PokemonAbility>) =
        liveData(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val idsOfAbilitiesToFetch = pokemon.abilityIds.filterNot { pokemonAbilityId ->
                    abilities.map { it.id }.any { abilityId -> pokemonAbilityId == abilityId }
                }
                idsOfAbilitiesToFetch.map {
                    async {
                        fetchAndSavePokemonAbility(it, pokemon)
                    }
                }.awaitAll()
                emit(
                    Resource.success(
                        pokemonAbilityMetaDataRepository.getPokemonWithAbilitiesAndMetaDataByIdAsync(
                            pokemon.id
                        )
                    )
                )
            }
        }

    private suspend fun fetchAndSavePokemonAbility(
        abilityId: Int,
        pokemon: Pokemon,
    ) {
        withContext(Dispatchers.IO) {
            val abilityRequest = remotePokemonRepository.abilityForId(abilityId)
            when (abilityRequest.status) {
                Status.SUCCESS -> {
                    Log.d("PAVM", "abilityRequest ${abilityRequest.data.toString()}")
                    abilityRequest.data?.let {
                        insertPokemonAbility(
                            pokemon.id,
                            PokemonAbility.mapRemotePokemonAbilityToDatabasePokemonAbility(it)
                        )
                        insertPokemonAbilityMetaDataJoin(pokemon.id, it.id)
                    }
                }
                else -> {
                }
            }
        }
    }

    private suspend fun insertPokemonAbility(remotePokemonId: Int, pokemonAbility: PokemonAbility) {
        withContext(Dispatchers.IO) {
            pokemonAbilityRepository.insertPokemonAbility(pokemonAbility)
            pokemonAbilityRepository.insertPokemonAbilityJoin(
                PokemonAbilityJoin(
                    remotePokemonId,
                    pokemonAbility.id
                )
            )
        }
    }

    private suspend fun joinMetaDataToAbility(
        pokemonWithAbilitiesAndMetaData: PokemonWithAbilitiesAndMetaData,
        pokemon: Pokemon
    ) {
        val idsOfAbilitiesNotJoined = pokemonWithAbilitiesAndMetaData.abilities.filterNot { pokemonAbility ->
            pokemonWithAbilitiesAndMetaData.pokemonAbilityMetaData.any { metaData -> pokemonAbility.name == metaData.abilityName }
        }.map { it.id }

        idsOfAbilitiesNotJoined.forEach {
            viewModelScope.launch {
                insertPokemonAbilityMetaDataJoin(pokemon.id, it)
            }
        }
    }

    private suspend fun insertPokemonAbilityMetaDataJoin(remotePokemonId: Int, pokemonAbilityId: Int) {
        withContext(Dispatchers.IO) {
            pokemonAbilityMetaDataRepository.insertAbilityMetaDataJoin(
                PokemonAbilityMetaDataJoin(
                    remotePokemonId,
                    PokemonMoveMetaData.createMetaMoveId(remotePokemonId, pokemonAbilityId)
                )
            )
        }
    }

    fun setPokemon(pokemon: Pokemon) {
        Log.d("PAVM", "SET POKEMON $pokemon")
        this.pokemon.value = pokemon
    }

    fun retry() {
        this.pokemon.value = this.pokemon.value
    }

}

