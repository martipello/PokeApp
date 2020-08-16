package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonType (

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = POKEMON_TYPE_ID)
    var id: Int,

    @ColumnInfo(name = POKEMON_TYPE_NAME)
    var name: String,

    @ColumnInfo(name = POKEMON_TYPE_SLOT)
    var slot: Int

) {

    companion object {

        const val POKEMON_TYPE_ID: String = "type_id"
        const val POKEMON_TYPE_NAME: String = "type_name"
        const val POKEMON_TYPE_SLOT: String = "type_slot"

        fun getTypesInOrder(types: List<PokemonType>): List<PokemonType> {
            return types.sortedBy { it.slot }
        }

    }

}
