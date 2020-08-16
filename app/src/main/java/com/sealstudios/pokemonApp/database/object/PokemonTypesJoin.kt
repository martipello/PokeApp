package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin.Companion.POKEMON_TYPE_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, POKEMON_TYPE_ID])
class PokemonTypesJoin(
    @NotNull
    @ColumnInfo(name = POKEMON_ID, index = true)
    val pokemon_id: Int,

    @NotNull
    @ColumnInfo(name = POKEMON_TYPE_ID, index = true)
    val pokemon_type_id: Int

) {
    companion object {

        const val POKEMON_ID: String = "id"
        const val POKEMON_TYPE_ID: String = "type_id"

    }
}