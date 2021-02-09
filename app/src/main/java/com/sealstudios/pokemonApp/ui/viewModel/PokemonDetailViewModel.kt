package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.*
import com.sealstudios.pokemonApp.api.`object`.PokemonAbility
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.mapDbPokemonFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility.Companion.getPokemonAbilityIdFromUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonMove.Companion.getPokemonMoveIdFromUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.mapDbPokemonTypesFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin.Companion.mapTypeJoinsFromPokemonResponse
import com.sealstudios.pokemonApp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonDetailViewModel @ViewModelInject constructor(
        private val repository: PokemonWithTypesAndSpeciesRepository,
        private val remotePokemonRepository: RemotePokemonRepository,
        private val pokemonTypeRepository: PokemonTypeRepository,
        private val pokemonBaseStatsRepository: PokemonBaseStatsRepository,
        private val pokemonWithTypesRepository: PokemonWithTypesRepository,
        private val pokemonMoveMetaDataRepository: PokemonMoveMetaDataRepository,
        private val pokemonAbilityMetaDataRepository: PokemonAbilityMetaDataRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var revealAnimationExpanded: MutableLiveData<Boolean> = getRevealAnimationExpandedState()

    private var pokemonId: MutableLiveData<Int> = getPokemonIdSavedState()

    val pokemonDetail: LiveData<Resource<PokemonWithTypes>> = pokemonDetails()

    val onFinishedSavingPokemonAbilities: SingleLiveEvent<Int> = SingleLiveEvent()
    val onFinishedSavingPokemonBaseStats: SingleLiveEvent<Int> = SingleLiveEvent()
    val onFinishedSavingPokemonMoves: SingleLiveEvent<Int> = SingleLiveEvent()

    private fun pokemonDetails() = pokemonId.switchMap { id ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithTypes = pokemonWithTypesRepository.getSinglePokemonWithTypesByIdAsync(id)
            if (pokemonWithTypes.pokemon.isDefault()) {
                emitSource(fetchPokemonDetails(pokemonWithTypes))
            } else {
                onFinish(id, null)
                emit(
                        Resource.success(
                                PokemonWithTypes(
                                        pokemon = pokemonWithTypes.pokemon,
                                        types = pokemonWithTypes.types
                                )
                        )
                )
            }
        }
    }

    private suspend fun onFinish(pokemonId: Int, pokemonRequestData: ApiPokemon?) {
        if (pokemonRequestData != null) {
            onFinishedSavingPokemonBaseStats(pokemonId, pokemonRequestData)
            onFinishedSavingPokemonAbilities(pokemonId, pokemonRequestData)
            onFinishedSavingPokemonMoves(pokemonId, pokemonRequestData)
        } else {
            onFinishedSavingPokemonAbilities.value = pokemonId
            onFinishedSavingPokemonBaseStats.value = pokemonId
            onFinishedSavingPokemonMoves.value = pokemonId
        }
    }

    private fun fetchPokemonDetails(pokemonWithTypes: PokemonWithTypes) = liveData {
        val pokemonRequest = remotePokemonRepository.pokemonForId(pokemonWithTypes.pokemon.id)
        when (pokemonRequest.status) {
            Status.SUCCESS -> {
                if (pokemonRequest.data != null) {
                    val pokemonRequestData = pokemonRequest.data
                    val pokemon = mapDbPokemonFromPokemonResponse(pokemonRequestData)
                    repository.updatePokemon(pokemon)
                    insertPokemonTypes(pokemonRequestData)
                    emit(
                            Resource.success(
                                    mapDbPokemonTypesFromPokemonResponse(pokemonRequestData)?.let {
                                        PokemonWithTypes(pokemon = pokemon, types = it)
                                    }
                            )
                    )
                    onFinish(pokemon.id, pokemonRequestData)
                } else {
                    emit(Resource.error(pokemonRequest.message
                            ?: "Data is empty", null, pokemonRequest.code))
                }
            }
            Status.ERROR -> emit(Resource.error(pokemonRequest.message
                    ?: "General error", null, pokemonRequest.code))
            Status.LOADING -> emit(Resource.loading(null))
        }
    }

    private suspend fun onFinishedSavingPokemonAbilities(pokemonId: Int, pokemonRequestData: ApiPokemon) {
        viewModelScope.launch {
            val updateDatabase = async {
                pokemonRequestData.abilities?.let { insertPokemonAbilityMetaData(it, pokemonId) }
            }
            updateDatabase.await()
            onFinishedSavingPokemonAbilities.value = pokemonId
        }
    }

    private suspend fun onFinishedSavingPokemonBaseStats(pokemonId: Int, pokemonRequestData: ApiPokemon) {
        viewModelScope.launch {
            val updateDatabase = async {
                pokemonRequestData.stats?.let { insertPokemonStats(it, pokemonId) }
            }
            updateDatabase.await()
            onFinishedSavingPokemonBaseStats.value = pokemonId
        }
    }

    private suspend fun onFinishedSavingPokemonMoves(pokemonId: Int, pokemonRequestData: ApiPokemon) {
        viewModelScope.launch {
            val updateDatabase = async {
                pokemonRequestData.moves?.let { insertPokemonMoveMetaData(it, pokemonId) }
            }
            updateDatabase.await()
            onFinishedSavingPokemonMoves.value = pokemonId
        }
    }

    private suspend fun insertPokemonTypes(
            remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {
            mapDbPokemonTypesFromPokemonResponse(
                    remotePokemon
            )?.let {
                pokemonTypeRepository.insertPokemonTypes(it)
            }
            mapTypeJoinsFromPokemonResponse(
                    remotePokemon
            )?.let {
                pokemonTypeRepository.insertPokemonTypeJoins(it)
            }
        }
    }

    private suspend fun insertPokemonStats(
            stats: List<PokemonStat>,
            remotePokemonId: Int
    ) {
        withContext(Dispatchers.IO) {
            val pokemonBaseStats = PokemonBaseStats.mapRemoteStatToPokemonBaseStat(
                    pokemonId = remotePokemonId,
                    pokemonStats = stats
            )
            val pokemonBaseStatsJoin = PokemonBaseStatsJoin(remotePokemonId, pokemonBaseStats.id)
            pokemonBaseStatsRepository.insertPokemonBaseStats(pokemonBaseStats)
            pokemonBaseStatsRepository.insertPokemonBaseStatsJoin(pokemonBaseStatsJoin)
        }
    }

    private suspend fun insertPokemonMoveMetaData(moves: List<PokemonMoveResponse>, pokemonId: Int) {
        for (moveResponse in moves) {
            val moveId = getPokemonMoveIdFromUrl(moveResponse.move.url)
            withContext(Dispatchers.IO) {
                val moveMetaData = PokemonMoveMetaData.mapRemotePokemonToMoveMetaData(
                        moveId,
                        pokemonId,
                        moveResponse.move.name,
                        moveResponse.version_group_details
                )
                pokemonMoveMetaDataRepository.insertMoveMetaData(moveMetaData)
            }
        }
    }

    private suspend fun insertPokemonAbilityMetaData(abilities: List<PokemonAbility>, pokemonId: Int) {
        withContext(Dispatchers.IO) {
            for (abilityResponse in abilities) {
                val abilityId = getPokemonAbilityIdFromUrl(abilityResponse.ability.url)
                withContext(Dispatchers.IO) {
                    val abilityMetaData = PokemonAbilityMetaData.mapRemotePokemonToAbilityMetaData(
                            abilityId,
                            pokemonId,
                            abilityResponse.ability.name,
                            abilityResponse.isHidden
                    )
                    pokemonAbilityMetaDataRepository.insertAbilityMetaData(abilityMetaData)
                }
            }
        }

    }

    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
        savedStateHandle.set(PokemonDetailViewModel.pokemonId, pokemonId)
    }

    private fun getPokemonIdSavedState(): MutableLiveData<Int> {
        val id = savedStateHandle.get<Int>(PokemonDetailViewModel.pokemonId) ?: -1
        return MutableLiveData(id)
    }

    fun setRevealAnimationExpandedState(hasExpanded: Boolean) {
        revealAnimationExpanded.value = hasExpanded
        savedStateHandle.set(hasExpandedKey, hasExpanded)
    }

    private fun getRevealAnimationExpandedState(): MutableLiveData<Boolean> {
        val hasExpanded = savedStateHandle.get<Boolean>(hasExpandedKey) ?: false
        return MutableLiveData(hasExpanded)
    }

    companion object {
        private const val pokemonId: String = "pokemonId"
        private const val hasExpandedKey: String = "hasExpanded"
    }

}

