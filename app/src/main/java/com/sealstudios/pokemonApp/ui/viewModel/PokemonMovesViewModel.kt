package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.Resource
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMoveMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMovesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithMovesAndMetaData
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonMoveWithMetaData
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonMoveWithMetaData.Companion.separateByGeneration
import com.sealstudios.pokemonApp.repository.PokemonMoveMetaDataRepository
import com.sealstudios.pokemonApp.repository.PokemonMoveRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.GenerationHeader
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveAdapterItem
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.GenerationHeaderViewHolder
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonMoveViewHolder
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import kotlinx.coroutines.*

class PokemonMovesViewModel @ViewModelInject constructor(
        private val moveRepository: PokemonMoveRepository,
        private val pokemonMoveMetaDataRepository: PokemonMoveMetaDataRepository,
        private val remotePokemonRepository: RemotePokemonRepository,
) : ViewModel() {

    private var pokemonId: MutableLiveData<Int> = MutableLiveData()

    // Doesn't handle errors as there isn't a way to emit them from the for loop in fetchPokemonMoves
    // and meta data comes from the pokemon and not the move meaning we would double the calls to the API

    val pokemonMoves: LiveData<Resource<MutableList<PokemonMoveAdapterItem>>> = pokemonId.switchMap { pokemonId ->
        liveData {
            emit(Resource.loading(null))
            val pokemonWithMovesAndMetaData = moveRepository.getPokemonMovesAndMetaDataByIdAsync(pokemonId)
            if (pokemonWithMovesAndMetaData.moves.size != pokemonWithMovesAndMetaData.pokemon.move_ids.size) {
                emitSource(fetchPokemonMoves(pokemonWithMovesAndMetaData.pokemon, pokemonWithMovesAndMetaData.moves))
            } else {
                val movesAndHeaders = prepareListForAdapter(pokemonWithMovesAndMetaData)
                emit(Resource.success(movesAndHeaders))
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
                async { fetchAndSavePokemonMove(it, pokemon) }
            }.awaitAll()

            val pokemonWithMovesAndMetaData = moveRepository.getPokemonMovesAndMetaDataByIdAsync(pokemon.id)
            val movesAndHeaders = prepareListForAdapter(pokemonWithMovesAndMetaData)
            emit(Resource.success(movesAndHeaders))
        }
    }

    private suspend fun prepareListForAdapter(pokemonWithMovesAndMetaData: PokemonWithMovesAndMetaData): MutableList<PokemonMoveAdapterItem> {
        return withContext(Dispatchers.Default) {
            val movesWithMetaDataList = mapPokemonWithMovesAndMetaDataToMovesWithMetaDataAsync(pokemonWithMovesAndMetaData).await()
            val movesSeparatedByGeneration = movesWithMetaDataList.separateByGeneration()
            return@withContext mapMovesToHeadersAsync(movesSeparatedByGeneration).await()
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

    fun setPokemonId(pokemonId: Int) {
        this.pokemonId.value = pokemonId
    }

    fun retry() {
        this.pokemonId.value = this.pokemonId.value
    }


    private suspend fun mapMovesToHeadersAsync(pokemonMoves: Map<String, List<PokemonMoveWithMetaData>?>) =
            withContext(Dispatchers.IO) {
                return@withContext async {
                    val pokemonMoveList = mutableListOf<PokemonMoveAdapterItem>()
                    for (moveEntry in pokemonMoves.entries) {
                        pokemonMoveList.add(
                                createPokemonMoveAdapterHeaderItem(moveEntry.key)
                        )
                        if (!moveEntry.value.isNullOrEmpty()) {
                            pokemonMoveList.addAll(moveEntry.value!!.map {
                                createPokemonMoveAdapterListItem(it)
                            })
                        }
                    }
                    pokemonMoveList
                }
            }

    private suspend fun mapPokemonWithMovesAndMetaDataToMovesWithMetaDataAsync(pokemonWithMovesAndMetaData: PokemonWithMovesAndMetaData) =
            withContext(Dispatchers.Default) {
                return@withContext async {
                    val pokemonMoveWithMetaDataList = mutableListOf<PokemonMoveWithMetaData>()
                    pokemonWithMovesAndMetaData.moves.forEach { move ->
                        val movesToAdd = pokemonWithMovesAndMetaData.pokemonMoveMetaData.filter { moveMetaData ->
                            moveMetaData.moveName == move.name
                        }
                        movesToAdd.forEach {
                            pokemonMoveWithMetaDataList.add(PokemonMoveWithMetaData(move, it))
                        }
                    }
                    pokemonMoveWithMetaDataList
                }
            }

    private suspend fun createPokemonMoveAdapterListItem(pokemonMoveWithMetaData: PokemonMoveWithMetaData): PokemonMoveAdapterItem {
        return withContext(Dispatchers.IO) {
            return@withContext PokemonMoveAdapterItem(
                    moveWithMetaData = pokemonMoveWithMetaData,
                    header = null,
                    itemType = PokemonMoveViewHolder.layoutType
            )
        }
    }

    private suspend fun createPokemonMoveAdapterHeaderItem(headerName: String): PokemonMoveAdapterItem {
        return withContext(Dispatchers.IO) {
            return@withContext PokemonMoveAdapterItem(
                    moveWithMetaData = null,
                    header = GenerationHeader(
                            headerName = PokemonGeneration.formatGenerationName(
                                    PokemonGeneration.getGeneration(headerName)
                            )
                    ),
                    itemType = GenerationHeaderViewHolder.layoutType
            )
        }
    }

    companion object {
        const val TAG = "MOVES_VM"
    }


}

