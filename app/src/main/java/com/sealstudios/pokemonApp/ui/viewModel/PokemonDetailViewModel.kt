package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
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
import kotlinx.coroutines.launch

class PokemonDetailViewModel @ViewModelInject constructor(
    private val repository: PokemonWithTypesAndSpeciesAndMovesRepository,
    private val moveRepository: PokemonMoveRepository,
    private val moveJoinRepository: PokemonMoveJoinRepository,
    private val remotePokemonRepository: RemotePokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pokemon: LiveData<PokemonWithTypesAndSpeciesAndMoves>
    private var id: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        pokemon = Transformations.distinctUntilChanged(Transformations.switchMap(id) { id ->
            val pokemon = repository.getSinglePokemonById(id)
            pokemon.map {
                Log.d("PDVM", "mapping")
                it.pokemon.move_ids.forEach { moveId ->
                    Log.d("PDVM", "fetchMovesForId $moveId")
                    fetchMovesForId(moveId = moveId, remotePokemonId = id)
                }
            }
            pokemon
        })

    }

    fun setId(id: Int) {
        this.id.value = id
    }

    private fun fetchMovesForId(
        moveId: Int,
        remotePokemonId: Int
    ) {
        viewModelScope.launch {
            val pokemonMovesRequest =
                remotePokemonRepository.movesForId(moveId)
            pokemonMovesRequest.let { pokemonMovesResponse ->
                if (pokemonMovesResponse.isSuccessful) {
                    Log.d("PDVM", "pokemonMovesResponse ${pokemonMovesResponse.body()}")
                    pokemonMovesResponse.body()?.let { move ->
                        insertPokemonMove(
                            remotePokemonId,
                            PokemonMove.mapRemotePokemonMoveToDatabasePokemonMove(move)
                        )
                    }
                }
            }

        }
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

