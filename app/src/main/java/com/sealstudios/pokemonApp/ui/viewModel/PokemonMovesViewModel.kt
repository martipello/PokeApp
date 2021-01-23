package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.repository.PokemonMoveMetaDataRepository
import com.sealstudios.pokemonApp.repository.PokemonMoveRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonMovesViewModel @ViewModelInject constructor(
    private val moveRepository: PokemonMoveRepository,
    private val pokemonMoveMetaDataRepository: PokemonMoveMetaDataRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemon: MutableLiveData<Pokemon> = MutableLiveData()

    val pokemonMoves: LiveData<PokemonWithMovesAndMetaData> = pokemon.switchMap {
        liveData {
            emitSource(moveRepository.getPokemonMovesAndMetaDataById(it.id))
        }
    }

    init {
        viewModelScope.launch {
            pokemon.asFlow().collect { pokemon ->
                val pokemonWithMovesAndMetaData = moveRepository.getPokemonMovesAndMetaDataByIdAsync(pokemon.id)
                if (pokemonWithMovesAndMetaData.moves.size != pokemonWithMovesAndMetaData.pokemon.move_ids.size) {
                    fetchPokemonMoves(pokemon, pokemonWithMovesAndMetaData.moves)
                }
                if (pokemonWithMovesAndMetaData.moves.size > pokemonWithMovesAndMetaData.pokemonMoveMetaData.size) {

                    val idsOfMovesNotJoined = pokemonWithMovesAndMetaData.moves.filterNot { pokemonMove ->
                        pokemonWithMovesAndMetaData.pokemonMoveMetaData.any { metaData -> pokemonMove.name == metaData.moveName }
                    }.map { it.id }

                    idsOfMovesNotJoined.forEach {
                        viewModelScope.launch {
                            insertPokemonMoveMetaDataJoin(pokemon.id, it)
                        }
                    }
                }
            }
        }
    }


    private fun fetchPokemonMoves(pokemon: Pokemon, moves: List<PokemonMove>) {
        val idsOfMovesToFetch = pokemon.move_ids.filterNot { pokemonMoveId ->
            moves.map { it.id }.any { moveId -> pokemonMoveId == moveId }
        }
        idsOfMovesToFetch.forEach {
            viewModelScope.launch {
                fetchAndSavePokemonMove(it, pokemon)
            }
        }
    }

    private suspend fun fetchAndSavePokemonMove(
        moveId: Int,
        pokemon: Pokemon,
    ) {
        withContext(Dispatchers.IO) {
            val moveRequest = remotePokemonRepository.moveForId(moveId)
            when (moveRequest.status) {
                Status.SUCCESS -> {
                    moveRequest.data?.let {
                        insertPokemonMove(
                            pokemon.id,
                            PokemonMove.mapRemotePokemonMoveToDatabasePokemonMove(it)
                        )
                        insertPokemonMoveMetaDataJoin(pokemon.id, it.id)
                    }
                }
                Status.ERROR -> Log.d(TAG, "Error getting pokemon move error")
                Status.LOADING -> Log.d(TAG, "Getting pokemon move loading")
            }
        }
    }

    private suspend fun insertPokemonMove(remotePokemonId: Int, pokemonMove: PokemonMove) {
        withContext(Dispatchers.IO) {
            moveRepository.insertPokemonMove(pokemonMove)
            moveRepository.insertPokemonMovesJoin(
                PokemonMovesJoin(
                    remotePokemonId,
                    pokemonMove.id
                )
            )
        }
    }

    private suspend fun insertPokemonMoveMetaDataJoin(remotePokemonId: Int, pokemonMoveId: Int) {
        withContext(Dispatchers.IO) {
            pokemonMoveMetaDataRepository.insertMoveMetaDataJoin(
                PokemonMoveMetaDataJoin(
                    remotePokemonId,
                    PokemonMoveMetaData.createMetaMoveId(remotePokemonId, pokemonMoveId)
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

