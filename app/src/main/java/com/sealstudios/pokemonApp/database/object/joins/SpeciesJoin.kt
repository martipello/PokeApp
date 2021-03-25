package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.Species.Companion.SPECIES_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, SPECIES_ID])
class SpeciesJoin(
        @NotNull
        @ColumnInfo(name = POKEMON_ID, index = true)
        val pokemon_id: Int,

        @NotNull
        @ColumnInfo(name = SPECIES_ID, index = true)
        val species_id: Int

)