package com.sealstudios.pokemonApp.database.`object`.wrappers

import com.sealstudios.pokemonApp.database.`object`.Move
import com.sealstudios.pokemonApp.database.`object`.MoveMetaData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class MoveWithMetaData(
        val move: Move,
        val moveMetaData: MoveMetaData
) {
    companion object {

        suspend fun List<MoveWithMetaData>.separateByGeneration() =
                suspendCancellableCoroutine<Map<String, List<MoveWithMetaData>?>> { continuation ->
                    val moveMap = mutableMapOf<String, MutableList<MoveWithMetaData>?>()
                    this.forEach {
                        if (it.move.generation.isNotEmpty()) {
                            if (moveMap.containsKey(it.move.generation)) {
                                val list = moveMap[it.move.generation]
                                list?.add(it)
                                moveMap[it.move.generation] = list
                            } else {
                                moveMap[it.move.generation] = mutableListOf(it)
                            }
                        }
                    }
                    continuation.resume(moveMap)
                }

    }
}