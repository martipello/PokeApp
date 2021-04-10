package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityJoin
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithAbilitiesAndMetaData
import com.sealstudios.pokemonApp.database.`object`.wrappers.AbilityWithMetaData
import com.sealstudios.pokemonApp.repository.AbilityMetaDataRepository
import com.sealstudios.pokemonApp.repository.AbilityRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.*

class AbilityViewModel @ViewModelInject constructor(
        private val abilityRepository: AbilityRepository,
        private val abilityMetaDataRepository: AbilityMetaDataRepository,
        private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = MutableLiveData()

    // Doesn't handle errors as there isn't a way to emit them from the for loop in fetchPokemonAbilities
    // and meta data comes from the pokemon and not the ability meaning we would double the calls to the API

    val abilities: LiveData<Resource<List<AbilityWithMetaData>>> = pokemonId.switchMap { pokemonId ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithAbilitiesAndMetaData =
                    abilityMetaDataRepository.getPokemonWithAbilitiesAndMetaDataByIdAsync(pokemonId)
            if (pokemonWithAbilitiesAndMetaData.abilities.size > pokemonWithAbilitiesAndMetaData.abilityMetaData.size) {
                joinMetaDataToAbilities(pokemonWithAbilitiesAndMetaData, pokemonWithAbilitiesAndMetaData.pokemon)
            }

            if (pokemonWithAbilitiesAndMetaData.pokemon.abilityIds.size != pokemonWithAbilitiesAndMetaData.abilities.size) {
                emitSource(fetchPokemonAbilities(
                        pokemonWithAbilitiesAndMetaData.pokemon,
                        pokemonWithAbilitiesAndMetaData.abilities.map { it.id }))
            } else {
                val mappedPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData = mapPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData(pokemonWithAbilitiesAndMetaData)
                emit(Resource.success(mappedPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData))
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
                                    mapPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData(abilityMetaDataRepository.getPokemonWithAbilitiesAndMetaDataByIdAsync(pokemon.id))
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
                                Ability.mapRemotePokemonAbilityToDatabasePokemonAbility(it)
                        )
                        insertPokemonAbilityMetaDataJoin(pokemon.id, it.id)
                    }
                }
                else -> {
                }
            }
        }
    }

    private suspend fun insertPokemonAbility(remotePokemonId: Int, ability: Ability) {
        withContext(Dispatchers.IO) {
            abilityRepository.insertPokemonAbility(ability)
            insertPokemonAbilityJoin(remotePokemonId, ability.id)
        }
    }

    private suspend fun insertPokemonAbilityJoin(remotePokemonId: Int, pokemonAbilityId: Int) {
        withContext(Dispatchers.IO) {
            abilityRepository.insertPokemonAbilityJoin(
                    AbilityJoin(
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
                pokemonWithAbilitiesAndMetaData.abilityMetaData.any { metaData -> pokemonAbility.name == metaData.abilityName }
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
            abilityMetaDataRepository.insertAbilityMetaDataJoin(
                    AbilityMetaDataJoin.createPokemonAbilityMetaDataJoin(remotePokemonId = remotePokemonId, pokemonAbilityId = pokemonAbilityId)
            )
        }
    }


    private suspend fun mapPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData(pokemonWithAbilitiesAndMetaData: PokemonWithAbilitiesAndMetaData): List<AbilityWithMetaData> {
        return withContext(Dispatchers.Default) {
            return@withContext pokemonWithAbilitiesAndMetaData.abilities.map { ability ->
                pokemonWithAbilitiesAndMetaData.abilityMetaData.filter {
                    it.id == AbilityMetaData.createAbilityMetaDataId(
                            pokemonWithAbilitiesAndMetaData.pokemon.id,
                            ability.id
                    )
                }.map { AbilityWithMetaData(ability, it) }
            }.flatten()
        }

    }


    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

    companion object {
        const val TAG = "ABILITY_VM"
    }

}

