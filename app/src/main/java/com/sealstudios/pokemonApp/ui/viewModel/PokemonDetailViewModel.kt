package com.sealstudios.pokemonApp.ui.viewModel

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.mapDbPokemonFromPokemonResponse
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.repository.PokemonRepository
import com.sealstudios.pokemonApp.repository.PokemonWithTypesRepository
import com.sealstudios.pokemonApp.repository.RemotePokemonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.sealstudios.pokemonApp.database.`object`.Pokemon as dbPokemon

class PokemonDetailViewModel @ViewModelInject constructor(
    private val repository: PokemonWithTypesRepository,
    private val remoteRepository: RemotePokemonRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val localPokemon: LiveData<PokemonWithTypesAndSpecies>
    private var search: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        localPokemon = Transformations.distinctUntilChanged(Transformations.switchMap(search) {
            repository.getSinglePokemonWithTypeAndSpeciesById(it)
        })
    }

    fun setSearch(search: Int) {
        this.search.value = search
    }

    suspend fun getRemotePokemonDetail(id: Int) {
        val pokemon = remoteRepository.getRemotePokemonSpeciesForId(id)
        Log.d("PDV", "${pokemon.body()}")
    }

    companion object {
        //TODO have this return an object and save it somewhere else to remove localRepository: PokemonRepository
        fun getRemotePokemonDetail(
            scope: CoroutineScope,
            dbPokemon: dbPokemon,
            remoteRepository: RemotePokemonRepository,
            localRepository: PokemonRepository
        ) {
            scope.launch {
                val pokemon = remoteRepository.getRemotePokemonById(dbPokemon.id).body()
                val pokemonSpeciesById =
                    remoteRepository.getRemotePokemonSpeciesForId(dbPokemon.id).body()
                pokemon?.let { apiPokemon ->
                    val mappedPokemon = mapDbPokemonFromPokemonResponse(apiPokemon)
                    pokemonSpeciesById?.let { species ->
                        for (genus in species.genera) {
                            if (genus.language.name == "en") {
                                mappedPokemon.apply {
//                                    this.species = genus.genus
                                }
                            }
                        }

                    }
                    localRepository.insertPokemon(mappedPokemon)
                }
            }
        }


        //TODO remove this and put in the view model
//        private suspend fun fetchMovesForId(
//            remotePokemonId: Int
//        ) {
//            coroutineScope {
//                val pokemonMovesRequest =
//                    remotePokemonRepository.getRemotePokemonMovesForId(remotePokemonId)
//                pokemonMovesRequest.let { pokemonMovesResponse ->
//                    if (pokemonMovesResponse.isSuccessful) {
//                        pokemonMovesResponse.body()?.let { move ->
//                            insertPokemonMove(
//                                remotePokemonId,
//                                PokemonMove.mapRemotePokemonMoveToDatabasePokemonMove(move)
//                            )
//                        }
//                    }
//                }
//
//            }
//        }
//
//        //TODO remove this and put in the view model
//        private suspend fun insertPokemonMove(remotePokemonId: Int, pokemonMove: PokemonMove) {
//            localPokemonMoveRepository.insertPokemonMove(pokemonMove)
//            pokemonMoveJoinRepository.insertPokemonMovesJoin(
//                PokemonMovesJoin(
//                    remotePokemonId,
//                    pokemonMove.id
//                )
//            )
//        }

//    //TODO remove this and put in the view model
//    private suspend fun fetchAbilitiesForId(
//        remoteRepository: RemotePokemonRepository,
//        remotePokemonId: Int
//    ) {
//        coroutineScope {
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
//    //TODO remove this and put in the view model
//    private suspend fun insertPokemonAbility(remotePokemonId: Int, pokemonSpecies: PokemonAbility) {
//        localPokemonSpeciesRepository.insertPokemonSpecies(pokemonSpecies)
//        pokemonSpeciesJoinRepository.insertPokemonSpeciesJoin(
//            PokemonSpeciesJoin(
//                remotePokemonId,
//                pokemonSpecies.id
//            )
//        )
//    }

    }
}

