package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies.Companion.SPECIES_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies.Companion.SPECIES_NAME
import org.jetbrains.annotations.NotNull

data class PokemonSpeciesForList(

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = SPECIES_ID)
    var id: Int = 0,

    @ColumnInfo(name = SPECIES_NAME)
    var species: String = "",

    )