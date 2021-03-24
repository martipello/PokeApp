package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_IMAGE
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_NAME
import org.jetbrains.annotations.NotNull


open class PokemonForList(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_ID)
        var id: Int = 0,

        @ColumnInfo(name = POKEMON_NAME)
        var name: String = "",

        @ColumnInfo(name = POKEMON_IMAGE)
        var image: String = "",
)