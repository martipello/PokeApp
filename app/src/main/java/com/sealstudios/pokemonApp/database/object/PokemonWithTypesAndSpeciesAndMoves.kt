package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class PokemonWithTypesAndSpeciesAndMoves(
    @Embedded
    val pokemon: Pokemon,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonType::class,
        entityColumn = PokemonType.TYPE_ID,
        associateBy = Junction(
            value = PokemonTypesJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonType.TYPE_ID
        )
    )
    val types: List<PokemonType>,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonSpecies::class,
        entityColumn = PokemonSpecies.SPECIES_ID,
        associateBy = Junction(
            value = PokemonSpeciesJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonSpecies.SPECIES_ID
        )
    )
    val species: PokemonSpecies,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonMove::class,
        entityColumn = PokemonMove.MOVE_ID,
        associateBy = Junction(
            value = PokemonMovesJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonMove.MOVE_ID
        )
    )
    val moves: List<PokemonMove>
) {
    companion object {

        suspend fun List<PokemonMove>.getPokemonMoves() =
            suspendCancellableCoroutine<Map<String, List<PokemonMove>?>> { continuation ->
                val moveMap = mutableMapOf<String, MutableList<PokemonMove>?>()
                this.forEach {
                    if (it.generation.isNotEmpty()) {
                        if (moveMap.containsKey(it.generation)) {
                            val list = moveMap[it.generation]
                            list?.add(it)
                            moveMap[it.generation] = list
                        } else {
                            moveMap[it.generation] = mutableListOf(it)
                        }
                    }
                }
                continuation.resume(moveMap)
            }
    }

}