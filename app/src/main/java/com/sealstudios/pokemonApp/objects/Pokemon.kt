package com.sealstudios.pokemonApp.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
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
) {
    companion object {
        const val POKEMON_ID: String = "id"
        const val POKEMON_NAME: String = "name"
    }
}
