package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.Move.Companion.MOVE_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, MOVE_ID])
class MovesJoin(
        @NotNull
        @ColumnInfo(name = POKEMON_ID, index = true)
        val pokemon_id: Int,

        @NotNull
        @ColumnInfo(name = MOVE_ID, index = true)
        val move_id: Int

)