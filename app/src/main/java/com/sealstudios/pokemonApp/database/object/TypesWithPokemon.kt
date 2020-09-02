package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.TYPE_ID

data class TypesWithPokemon(
    @Embedded()
    val pokemonTypes: PokemonType,
    @Relation(
        parentColumn = TYPE_ID,
        entity = com.sealstudios.pokemonApp.database.`object`.Pokemon::class,
        entityColumn = POKEMON_ID,
        associateBy = Junction(
            value = PokemonTypesJoin::class,
            parentColumn = TYPE_ID,
            entityColumn = POKEMON_ID
        )
    )
    val pokemon: List<Pokemon>
)


