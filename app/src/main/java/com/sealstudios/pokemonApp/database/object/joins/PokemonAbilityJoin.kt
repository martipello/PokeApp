package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility.Companion.ABILITY_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, ABILITY_ID])
class PokemonAbilityJoin(
    @NotNull
    @ColumnInfo(name = POKEMON_ID, index = true)
    val pokemon_id: Int,

    @NotNull
    @ColumnInfo(name = ABILITY_ID, index = true)
    val ability_id: Int

)