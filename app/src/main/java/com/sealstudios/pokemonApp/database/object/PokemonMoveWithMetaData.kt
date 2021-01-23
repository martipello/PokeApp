package com.sealstudios.pokemonApp.database.`object`

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class PokemonMoveWithMetaData (
    val pokemonMoveMetaData: PokemonMoveMetaData,
    val pokemonMove: PokemonMove
){
    companion object {

        suspend fun List<PokemonMoveWithMetaData>.separateByGeneration() =
            suspendCancellableCoroutine<Map<String, List<PokemonMoveWithMetaData>?>> { continuation ->
                val moveMap = mutableMapOf<String, MutableList<PokemonMoveWithMetaData>?>()
                this.forEach {
                    if (it.pokemonMove.generation.isNotEmpty()) {
                        if (moveMap.containsKey(it.pokemonMove.generation)) {
                            val list = moveMap[it.pokemonMove.generation]
                            list?.add(it)
                            moveMap[it.pokemonMove.generation] = list
                        } else {
                            moveMap[it.pokemonMove.generation] = mutableListOf(it)
                        }
                    }
                }
                continuation.resume(moveMap)
            }
    }
}