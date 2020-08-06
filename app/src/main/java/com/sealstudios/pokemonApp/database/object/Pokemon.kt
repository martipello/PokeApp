package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.TABLE_NAME
import org.jetbrains.annotations.NotNull

@Entity(tableName = TABLE_NAME)
data class Pokemon(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_ID)
        var id: Int,

        @ColumnInfo(name = POKEMON_NAME)
        var name: String,

        @ColumnInfo(name = POKEMON_URL)
        var url: String,

        @ColumnInfo(name = POKEMON_WEIGHT)
        val weight: Int,

        @ColumnInfo(name = POKEMON_HEIGHT)
        val height: Int,

) {
    companion object {
        const val POKEMON_ID: String = "id"
        const val POKEMON_NAME: String = "name"
        const val POKEMON_URL: String = "url"
        const val POKEMON_HEIGHT: String = "height"
        const val MOVES: String = "moves"
        const val POKEMON_WEIGHT: String = "weight"
    }
}
