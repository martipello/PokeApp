package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.PokemonAbilityMetaDataRepository
import com.sealstudios.pokemonApp.repository.PokemonAbilityRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class PokemonAbilityViewModel @ViewModelInject constructor(
        private val pokemonAbilityRepository: PokemonAbilityRepository,
        private val pokemonAbilityMetaDataRepository: PokemonAbilityMetaDataRepository,
        private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = MutableLiveData()

    // Doesn't handle errors as there isn't a way to emit them from the for loop in fetchPokemonAbilities
    // and meta data comes from the pokemon and not the ability meaning we would double the calls to the API

    val pokemonAbilities: LiveData<Resource<PokemonWithAbilitiesAndMetaData>> = pokemonId.switchMap { pokemonId ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithAbilitiesAndMetaData =
                    pokemonAbilityMetaDataRepository.getPokemonWithAbilitiesAndMetaDataByIdAsync(pokemonId)
            if (pokemonWithAbilitiesAndMetaData.abilities.size > pokemonWithAbilitiesAndMetaData.pokemonAbilityMetaData.size) {
                joinMetaDataToAbilities(pokemonWithAbilitiesAndMetaData, pokemonWithAbilitiesAndMetaData.pokemon)
            }

            if (pokemonWithAbilitiesAndMetaData.pokemon.abilityIds.size != pokemonWithAbilitiesAndMetaData.abilities.size) {
                emitSource(
                        fetchPokemonAbilities(
                                pokemonWithAbilitiesAndMetaData.pokemon,
                                pokemonWithAbilitiesAndMetaData.abilities.map { it.id })
                )
            } else {
                emit(Resource.success(pokemonWithAbilitiesAndMetaData))
            }
        }
    }

    private suspend fun fetchPokemonAbilities(pokemon: Pokemon, abilityIds: List<Int>) =
            liveData(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    val idsOfAbilitiesToFetch = pokemon.abilityIds.filterNot { pokemonAbilityId ->
                        abilityIds.any { abilityId -> pokemonAbilityId == abilityId }
                    }
                    idsOfAbilitiesToFetch.map {
                        async {
                            fetchAndSavePokemonAbility(it, pokemon)
                        }
                    }.awaitAll()
                    emit(
                            Resource.success(
                                    pokemonAbilityMetaDataRepository.getPokemonWithAbilitiesAndMetaDataByIdAsync(pokemon.id)
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
            insertPokemonAbilityJoin(remotePokemonId, pokemonAbility.id)
        }
    }

    private suspend fun insertPokemonAbilityJoin(remotePokemonId: Int, pokemonAbilityId: Int) {
        withContext(Dispatchers.IO) {
            pokemonAbilityRepository.insertPokemonAbilityJoin(
                    PokemonAbilityJoin(
                            remotePokemonId,
                            pokemonAbilityId
                    )
            )
        }
    }

    private suspend fun joinMetaDataToAbilities(
            pokemonWithAbilitiesAndMetaData: PokemonWithAbilitiesAndMetaData,
            pokemon: Pokemon
    ) {
        withContext(Dispatchers.IO) {
            val idsOfAbilitiesNotJoined = pokemonWithAbilitiesAndMetaData.abilities.filterNot { pokemonAbility ->
                pokemonWithAbilitiesAndMetaData.pokemonAbilityMetaData.any { metaData -> pokemonAbility.name == metaData.abilityName }
            }.map { it.id }
            idsOfAbilitiesNotJoined.map {
                async {
                    insertPokemonAbilityMetaDataJoin(pokemon.id, it)
                }
            }.awaitAll()
        }
    }

    private suspend fun insertPokemonAbilityMetaDataJoin(remotePokemonId: Int, pokemonAbilityId: Int) {
        withContext(Dispatchers.IO) {
            pokemonAbilityMetaDataRepository.insertAbilityMetaDataJoin(
                    PokemonAbilityMetaDataJoin.createPokemonAbilityMetaDataJoin(remotePokemonId = remotePokemonId, pokemonAbilityId = pokemonAbilityId)
            )
        }
    }

    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

}

