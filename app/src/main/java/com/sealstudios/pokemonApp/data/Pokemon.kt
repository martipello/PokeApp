package com.sealstudios.pokemonApp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.TABLE_NAME
import org.jetbrains.annotations.NotNull

@Entity(tableName = TABLE_NAME)
data class Pokemon(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_ID)
        val id: Int,

        @ColumnInfo(name = POKEMON_NAME)
        val name: String,

        @ColumnInfo(name = POKEMON_URL)
        val url: String,
) {
    companion object {
        const val POKEMON_ID: String = "id"
        const val POKEMON_NAME: String = "name"
        const val POKEMON_URL: String = "url"
    }
}
