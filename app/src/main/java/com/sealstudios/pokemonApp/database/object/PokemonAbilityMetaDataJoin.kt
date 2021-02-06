package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData.Companion.ABILITY_META_DATA_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaData.Companion.META_MOVE_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, ABILITY_META_DATA_ID])
class PokemonAbilityMetaDataJoin(
    @NotNull
    @ColumnInfo(name = POKEMON_ID, index = true)
    val pokemon_id: Int,

    @NotNull
    @ColumnInfo(name = ABILITY_META_DATA_ID, index = true)
    val ability_meta_data_id: Int

)