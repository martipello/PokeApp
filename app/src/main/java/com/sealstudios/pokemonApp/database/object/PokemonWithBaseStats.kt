package com.sealstudios.pokemonApp.database.`object`

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PokemonWithBaseStats(
    @Embedded
    val pokemon: Pokemon,
    @Relation(
        parentColumn = Pokemon.POKEMON_ID,
        entity = PokemonBaseStats::class,
        entityColumn = PokemonBaseStats.POKEMON_BASE_STAT_ID,
        associateBy = Junction(
            value = PokemonBaseStatsJoin::class,
            parentColumn = Pokemon.POKEMON_ID,
            entityColumn = PokemonBaseStats.POKEMON_BASE_STAT_ID,
        )
    )
    val pokemonBaseStats: PokemonBaseStats?
)