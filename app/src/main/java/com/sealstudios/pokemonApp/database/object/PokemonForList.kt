package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_IMAGE
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_NAME
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_SPRITE
import org.jetbrains.annotations.NotNull


open class PokemonForList constructor(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = POKEMON_ID)
    var id: Int = 0,

    @ColumnInfo(name = POKEMON_NAME)
    var name: String = "",

    @ColumnInfo(name = POKEMON_IMAGE)
    var image: String = "",

    @ColumnInfo(name = POKEMON_SPRITE)
    var sprite: String? = "",

    )