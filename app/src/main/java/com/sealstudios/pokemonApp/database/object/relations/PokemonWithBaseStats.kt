package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.BaseStats
import com.sealstudios.pokemonApp.database.`object`.joins.BaseStatsJoin

data class PokemonWithBaseStats(
        @Embedded
        val pokemon: Pokemon,
        @Relation(
                parentColumn = Pokemon.POKEMON_ID,
                entity = BaseStats::class,
                entityColumn = BaseStats.POKEMON_BASE_STAT_ID,
                associateBy = Junction(
                        value = BaseStatsJoin::class,
                        parentColumn = Pokemon.POKEMON_ID,
                        entityColumn = BaseStats.POKEMON_BASE_STAT_ID,
                )
        )
        val baseStats: BaseStats?
)