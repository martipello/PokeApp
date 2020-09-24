package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonMovesJoin
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.repository.PokemonMoveJoinRepository
import com.sealstudios.pokemonApp.repository.PokemonMoveRepository
import com.sealstudios.pokemonApp.repository.PokemonWithTypesAndSpeciesAndMovesRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonDetailViewModel @ViewModelInject constructor(
    private val repository: PokemonWithTypesAndSpeciesAndMovesRepository,
    private val moveRepository: PokemonMoveRepository,
    private val moveJoinRepository: PokemonMoveJoinRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pokemon: LiveData<PokemonWithTypesAndSpeciesAndMoves>
    private var id: MutableLiveData<Int> = MutableLiveData(-1)
    var lightVibrantColorInt: MutableLiveData<Int> = getLightVibrantColor()
    var dominantColorInt: MutableLiveData<Int> = getDominantColor()
    var revealAnimationExpanded: MutableLiveData<Boolean> = getRevealAnimationExpandedState()

    init {
        pokemon = Transformations.distinctUntilChanged(Transformations.switchMap(id) { id ->
            val pokemonLiveData = repository.getSinglePokemonById(id)
            Transformations.switchMap(pokemonLiveData) { pokemon ->
                pokemon?.let {
                    viewModelScope.launch { maybeGetPokemonMoveIds(pokemon) }
                    MutableLiveData(pokemon)
                }
            }
        })
    }

    private suspend fun maybeGetPokemonMoveIds(pokemon: PokemonWithTypesAndSpeciesAndMoves) {
        withContext(Dispatchers.IO) {
            val moves = mutableListOf<PokemonMove>()
            pokemon.moves.forEach { pokemonMove ->
                if (pokemonMove.id > 0 && pokemonMove.generation.isEmpty()) {
                    try {
                        val move = fetchMovesForId(pokemonMove)
                        move?.let {
                            moves.add(move)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            savePokemonMoves(pokemon.pokemon.id, moves)
        }
    }

    private fun savePokemonMoves(
        pokemonId: Int,
        moves: MutableList<PokemonMove>
    ) {
        viewModelScope.launch { insertPokemonMoves(pokemonId, moves) }
    }

    fun setId(id: Int) {
        this.id.value = id
    }

    private suspend fun fetchMovesForId(
        pokemonMove: PokemonMove
    ): PokemonMove? {
        val pokemonMovesRequest = remotePokemonRepository.moveForId(pokemonMove.id)
        pokemonMovesRequest.let { pokemonMovesResponse ->
            if (pokemonMovesResponse.isSuccessful) {
                pokemonMovesResponse.body()?.let { move ->
                    return pokemonMove.copy(
                        accuracy = move.accuracy ?: 0,
                        pp = move.pp,
                        priority = move.priority,
                        power = move.power ?: 0,
                        generation = move.generation.name,
                        damage_class = move.damage_class.name,
                        type = move.type.name,
                        damage_class_effect_chance = move.effect_chance
                    )
                }
            } else {
                return null
            }
        }
        return null
    }

    private suspend fun insertPokemonMoves(remotePokemonId: Int, pokemonMoves: List<PokemonMove>) {
        withContext(Dispatchers.IO) {
            moveRepository.insertPokemonMove(pokemonMoves)
            val moveJoins = pokemonMoves.map { PokemonMovesJoin(remotePokemonId, it.id) }
            moveJoinRepository.insertPokemonMovesJoins(moveJoins)
        }
    }

    fun setRevealAnimationExpandedState(hasExpanded: Boolean) {
        revealAnimationExpanded.value = hasExpanded
        savedStateHandle.set(hasExpandedKey, hasExpanded)
    }

    fun setLightVibrantColor(lightVibrantColor: Int) {
        lightVibrantColorInt.value = lightVibrantColor
            savedStateHandle.set(lightVibrantColorKey, lightVibrantColor)
    }

    fun setDominantColor(dominantColor: Int) {
        dominantColorInt.value = dominantColor
        savedStateHandle.set(dominantColorKey, dominantColor)
    }

    private fun getRevealAnimationExpandedState(): MutableLiveData<Boolean> {
        val hasExpanded = savedStateHandle.get<Boolean>(hasExpandedKey) ?: false
        return MutableLiveData(hasExpanded)
    }

    private fun getLightVibrantColor(): MutableLiveData<Int> {
        val lightVibrantColor = savedStateHandle.get<Int>(lightVibrantColorKey) ?: 0
        return MutableLiveData(lightVibrantColor)
    }

    private fun getDominantColor(): MutableLiveData<Int> {
        val dominantColor = savedStateHandle.get<Int>(dominantColorKey) ?: 0
        return MutableLiveData(dominantColor)
    }

    companion object {
        private const val hasExpandedKey: String = "hasExpanded"
        private const val lightVibrantColorKey: String = "lightVibrantColor"
        private const val dominantColorKey: String = "dominantColor"
    }

//    private suspend fun insertPokemonMove(remotePokemonId: Int, pokemonMove: PokemonMove) {
//        moveRepository.insertPokemonMove(pokemonMove)
//        moveJoinRepository.insertPokemonMovesJoin(
//            PokemonMovesJoin(
//                remotePokemonId,
//                pokemonMove.id
//            )
//        )
//    }
//
//    private fun fetchAbilitiesForId(
//        remoteRepository: RemotePokemonRepository,
//        remotePokemonId: Int
//    ) {
//        viewModelScope.launch {
//            val pokemonAbilitiesRequest =
//                remoteRepository.getRemotePokemonAbilitiesForId(remotePokemonId)
//            pokemonAbilitiesRequest.let { pokemonAbilitiesResponse ->
//                if (pokemonAbilitiesResponse.isSuccessful) {
//                    pokemonAbilitiesResponse.body()?.let { ability ->
//                        insertPokemonAbility(remotePokemonId, ability)
//                    }
//                }
//            }
//
//        }
//    }
//
//    private suspend fun insertPokemonAbility(remotePokemonId: Int, ability: PokemonAbility) {
//        localPokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
//        pokemonSpeciesJoinRepository.insertPokemonSpeciesJoin(
//            PokemonSpeciesJoin(
//                remotePokemonId,
//                pokemonSpecies.id
//            )
//        )
//    }

}

