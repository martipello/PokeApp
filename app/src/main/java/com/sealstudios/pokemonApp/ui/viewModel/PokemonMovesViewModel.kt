package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.api.`object`.PokemonMoveResponse
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
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
            pokemonMoves =
                Transformations.distinctUntilChanged(Transformations.switchMap(pokemon) { pokemon ->
                    Transformations.distinctUntilChanged(
                        Transformations.switchMap(
                            moveRepository.getMovesByIds(
                                pokemon.move_ids
                            )
                        ) {
                            viewModelScope.launch {
                                val idsOfMovesToFetch = mutableListOf<Int>()
                                for (moveId in pokemon.move_ids) {
                                    if (!it.any { move -> move.id == moveId }) {
                                        // Move not in database
                                        idsOfMovesToFetch.add(moveId)
                                    }
                                }
                                fetchPokemonMoves(pokemon, idsOfMovesToFetch)
                            }
                            MutableLiveData(it)
                        })
                })
        }
    }

    private suspend fun fetchPokemonMoves(
        pokemon: Pokemon,
        idsToFetch: List<Int>,
    ) {
        if (idsToFetch.isNotEmpty()) {
            withContext(context = Dispatchers.IO) {
                val pokemonRequest =
                    remotePokemonRepository.pokemonById(pokemon.id)
                pokemonRequest.let { pokemonResponse ->
                    if (pokemonResponse.isSuccessful) {
                        pokemonResponse.body()?.let { pokemonSnapshot ->
                            val pokemonMoves = mutableListOf<PokemonMove>()
                            for (i in pokemonSnapshot.moves.indices) {
                                val moveApiResource = pokemonSnapshot.moves[i]
                                if (idsToFetch.any { it == getPokemonIdFromUrl(moveApiResource.move.url) }) {
                                    val partialPokemonMove =
                                        createPartialPokemonMove(moveApiResource, i, pokemon)
                                    val pokemonMove = fetchMoveDetail(partialPokemonMove)
                                    pokemonMove?.let {
                                        pokemonMoves.add(it)
                                    }
                                }
                                //TODO re-look at this, currently saves in batches of ten
                                // to stop observer firing and memory balance
                                if (pokemonMoves.size > 9) {
                                    savePokemonMoves(pokemon.id, pokemonMoves)
                                    pokemonMoves.clear()
                                } else if (i == pokemonSnapshot.moves.size - 1) {
                                    savePokemonMoves(pokemon.id, pokemonMoves)
                                    pokemonMoves.clear()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createPartialPokemonMove(
        moveApiResource: PokemonMoveResponse,
        i: Int,
        pokemon: Pokemon
    ): PokemonMove {
        return PokemonMove(
            id = getPokemonIdFromUrl(moveApiResource.move.url),
            name = moveApiResource.move.name,
            levelLearnedAt = if (i < pokemon.levelsLearnedAt.size) pokemon.levelsLearnedAt[i] else 0,
            learnMethod = if (i < pokemon.learnMethods.size) pokemon.learnMethods[i] else "N/A",
            versionLearnt = if (i < pokemon.versionsLearnt.size) pokemon.versionsLearnt[i] else "N/A",
        )
    }

    private suspend fun savePokemonMove(
        pokemonId: Int,
        move: PokemonMove?
    ) {
        move?.let {
            insertPokemonMove(pokemonId, move)
        }
    }

    private suspend fun savePokemonMoves(
        pokemonId: Int,
        moves: List<PokemonMove>
    ) {
        insertPokemonMoves(pokemonId, moves)
    }


    private suspend fun fetchMoveDetail(
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
            }
        }
        return null
    }

    private suspend fun insertPokemonMove(remotePokemonId: Int, pokemonMove: PokemonMove) {
        moveRepository.insertPokemonMove(pokemonMove)
        moveJoinRepository.insertPokemonMovesJoin(
            PokemonMovesJoin(
                remotePokemonId,
                pokemonMove.id
            )
        )
    }

    private suspend fun insertPokemonMoves(remotePokemonId: Int, pokemonMoves: List<PokemonMove>) {
        moveRepository.insertPokemonMoves(pokemonMoves)
        val moveJoins = pokemonMoves.map {
            PokemonMovesJoin(
                remotePokemonId,
                it.id
            )
        }
        moveJoinRepository.insertPokemonMovesJoins(moveJoins)
    }

    fun setPokemon(pokemon: Pokemon) {
        this.pokemon.value = pokemon
    }

}

