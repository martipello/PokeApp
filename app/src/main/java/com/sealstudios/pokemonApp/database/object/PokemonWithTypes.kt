package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PokemonWithTypes(
    @Embedded
    val pokemon: Pokemon,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonType::class,
        entityColumn = PokemonType.POKEMON_TYPE_ID,
        associateBy = Junction(
            value = PokemonTypesJoin::class,
            parentColumn = PokemonTypesJoin.POKEMON_ID,
            entityColumn = PokemonTypesJoin.POKEMON_TYPE_ID
        )
    )
    val types: List<PokemonType>
)