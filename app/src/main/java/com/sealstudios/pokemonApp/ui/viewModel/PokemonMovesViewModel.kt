package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMoveMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMovesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithMovesAndMetaData
import com.sealstudios.pokemonApp.repository.PokemonMoveMetaDataRepository
import com.sealstudios.pokemonApp.repository.PokemonMoveRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.*

class PokemonMovesViewModel @ViewModelInject constructor(
        private val moveRepository: PokemonMoveRepository,
        private val pokemonMoveMetaDataRepository: PokemonMoveMetaDataRepository,
        private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = MutableLiveData()

    // Doesn't handle errors as there isn't a way to emit them from the for loop in fetchPokemonMoves
    // and meta data comes from the pokemon and not the move meaning we would double the calls to the API

    val pokemonMoves: LiveData<Resource<PokemonWithMovesAndMetaData>> = pokemonId.switchMap { pokemonId ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithMovesAndMetaData = moveRepository.getPokemonMovesAndMetaDataByIdAsync(pokemonId)
            if (pokemonWithMovesAndMetaData.moves.size != pokemonWithMovesAndMetaData.pokemon.move_ids.size) {
                emitSource(fetchPokemonMoves(pokemonWithMovesAndMetaData.pokemon, pokemonWithMovesAndMetaData.moves))
            } else {
                emit(Resource.success(pokemonWithMovesAndMetaData))
            }

            if (pokemonWithMovesAndMetaData.moves.size > pokemonWithMovesAndMetaData.pokemonMoveMetaData.size) {
                joinMetaDataToMoves(pokemonWithMovesAndMetaData, pokemonWithMovesAndMetaData.pokemon)
            }
        }
    }

    private suspend fun fetchPokemonMoves(pokemon: Pokemon, moves: List<PokemonMove>) = liveData(Dispatchers.IO) {
        withContext(Dispatchers.IO) {
            val idsOfMovesToFetch = pokemon.move_ids.filterNot { pokemonMoveId ->
                moves.map { it.id }.any { moveId -> pokemonMoveId == moveId }
            }
            idsOfMovesToFetch.map {
                async {
                    fetchAndSavePokemonMove(it, pokemon)
                }
            }.awaitAll()
            emit(Resource.success(moveRepository.getPokemonMovesAndMetaDataByIdAsync(pokemon.id)))
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
                else -> {
                }
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

    private suspend fun joinMetaDataToMoves(
            pokemonWithMovesAndMetaData: PokemonWithMovesAndMetaData,
            pokemon: Pokemon
    ) {
        val idsOfMovesNotJoined = pokemonWithMovesAndMetaData.moves.filterNot { pokemonMove ->
            pokemonWithMovesAndMetaData.pokemonMoveMetaData.any { metaData -> pokemonMove.name == metaData.moveName }
        }.map { it.id }

        idsOfMovesNotJoined.forEach {
            viewModelScope.launch {
                insertPokemonMoveMetaDataJoin(pokemon.id, it)
            }
        }
    }

    private suspend fun insertPokemonMoveMetaDataJoin(remotePokemonId: Int, pokemonMoveId: Int) {
        withContext(Dispatchers.IO) {
            pokemonMoveMetaDataRepository.insertMoveMetaDataJoin(
                    PokemonMoveMetaDataJoin.createMoveMetaDataJoin(
                            remotePokemonId,
                            pokemonMoveId
                    )
            )
        }
    }

    fun setPokemon(pokemonId: Int) {
        this.pokemonId.value = pokemonId
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }

}

