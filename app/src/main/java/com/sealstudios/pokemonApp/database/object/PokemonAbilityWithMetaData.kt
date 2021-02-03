package com.sealstudios.pokemonApp.database.`object`

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class PokemonAbilityWithMetaData(
    val pokemonAbility: PokemonAbility,
    val pokemonAbilityMetaData: PokemonAbilityMetaData
) {

    companion object {

        suspend fun List<PokemonAbilityWithMetaData>.separateByGeneration() =
            suspendCancellableCoroutine<Map<String, List<PokemonAbilityWithMetaData>?>> { continuation ->
                val abilityMap = mutableMapOf<String, MutableList<PokemonAbilityWithMetaData>?>()
                this.forEach {
                    if (it.pokemonAbility.generation.isNotEmpty()) {
                        if (abilityMap.containsKey(it.pokemonAbility.generation)) {
                            val list = abilityMap[it.pokemonAbility.generation]
                            list?.add(it)
                            abilityMap[it.pokemonAbility.generation] = list
                        } else {
                            abilityMap[it.pokemonAbility.generation] = mutableListOf(it)
                        }
                    }
                }
                continuation.resume(abilityMap)
            }
    }
}