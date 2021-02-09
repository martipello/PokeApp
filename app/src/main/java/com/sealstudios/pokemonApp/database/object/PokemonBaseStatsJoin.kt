package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats.Companion.POKEMON_BASE_STAT_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, POKEMON_BASE_STAT_ID])
data class PokemonBaseStatsJoin(
    @NotNull
    @ColumnInfo(name = POKEMON_ID, index = true)
    val pokemon_id: Int,

    @NotNull
    @ColumnInfo(name = POKEMON_BASE_STAT_ID, index = true)
    val stats_id: Int

)