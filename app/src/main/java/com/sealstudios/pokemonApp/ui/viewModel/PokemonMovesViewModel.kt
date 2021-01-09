package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonMovesJoin
import com.sealstudios.pokemonApp.repository.PokemonMoveJoinRepository
import com.sealstudios.pokemonApp.repository.PokemonMoveRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonMovesViewModel @ViewModelInject constructor(
    private val moveRepository: PokemonMoveRepository,
    private val moveJoinRepository: PokemonMoveJoinRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    var pokemonMoves: LiveData<List<PokemonMove>> = MutableLiveData()
    private var pokemon: MutableLiveData<Pokemon> = MutableLiveData()

    init {
        viewModelScope.launch {
            pokemonMoves = liveData {
                pokemon.switchMap { pokemon ->
                    liveData<List<PokemonMove>> {
                        moveRepository.movesForIds(pokemon.move_ids).switchMap { moves ->
                            fetchAndSavePokemonMoves(pokemon, moves)
                            MutableLiveData(moves)
                        }.distinctUntilChanged()
                    }
                }.distinctUntilChanged()
            }
        }
    }

    private fun fetchAndSavePokemonMoves(
        pokemon: Pokemon,
        moves: List<PokemonMove>
    ) {
        val idsOfMovesToFetch = pokemon.move_ids.filterNot { pokemonMoveId ->
            moves.map { it.id }.any { moveId -> pokemonMoveId == moveId }
        }
        if (idsOfMovesToFetch.isNotEmpty()) {
            viewModelScope.launch {
//              Iterate pokemon.move_ids to get the indices needed for levelsLearnedAt, learnMethods and versionsLearnt.
                for (i in pokemon.move_ids) {
                    if (idsOfMovesToFetch.contains(pokemon.move_ids[i])) {
                        fetchAndSavePokemonMove(idsOfMovesToFetch[i], pokemon, i)
                    }
                }
            }
        }
    }

    private suspend fun fetchAndSavePokemonMove(
        moveId: Int,
        pokemon: Pokemon,
        learnLevelMethodVersionIndex: Int
    ) {
        withContext(Dispatchers.IO) {
            val moveRequest = remotePokemonRepository.moveForId(moveId)
            when (moveRequest.status) {
                Status.SUCCESS -> {
                    moveRequest.data?.let {
                        insertPokemonMove(
                            pokemon.id,
                            PokemonMove.mapRemotePokemonMoveToDatabasePokemonMove(it).copy(
                                levelLearnedAt = pokemon.levelsLearnedAt.elementAtOrElse(
                                    learnLevelMethodVersionIndex
                                ) { 0 },
                                learnMethod = pokemon.learnMethods.elementAtOrElse(
                                    learnLevelMethodVersionIndex
                                ) { "N/A" },
                                versionLearnt = pokemon.versionsLearnt.elementAtOrElse(
                                    learnLevelMethodVersionIndex
                                ) { "N/A" },
                            )
                        )
                    }
                }
                Status.ERROR -> Log.d(TAG, "Error getting pokemon move")
                Status.LOADING -> Log.d(TAG, "Getting pokemon move")
            }
        }

    }

    private suspend fun insertPokemonMove(remotePokemonId: Int, pokemonMove: PokemonMove) {
        withContext(Dispatchers.IO) {
            moveRepository.insertPokemonMove(pokemonMove)
            moveJoinRepository.insertPokemonMovesJoin(
                PokemonMovesJoin(
                    remotePokemonId,
                    pokemonMove.id
                )
            )
        }
    }

    fun setPokemon(pokemon: Pokemon) {
        this.pokemon.value = pokemon
    }

    companion object {
        const val TAG = "PMVM"
    }

}

