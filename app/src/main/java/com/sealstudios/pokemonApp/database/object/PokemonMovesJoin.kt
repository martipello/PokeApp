package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.PokemonMovesJoin.Companion.POKEMON_MOVE_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin.Companion.POKEMON_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, POKEMON_MOVE_ID])
class PokemonMovesJoin(
    @NotNull
    @ColumnInfo(name = POKEMON_ID, index = true)
    val pokemon_id: Int,

    @NotNull
    @ColumnInfo(name = POKEMON_MOVE_ID, index = true)
    val pokemon_movie_id: Int

) {
    companion object {

        const val POKEMON_ID: String = "id"
        const val POKEMON_MOVE_ID: String = "move_id"

    }
}