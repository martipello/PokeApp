package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sealstudios.pokemonApp.api.`object`.ApiPokemonType
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import org.jetbrains.annotations.NotNull

@TypeConverters(RoomIntListConverter::class)
@Entity
open class PokemonTypeMetaData constructor(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = TYPE_META_DATA_ID)
        var id: Int = 0,

        @ColumnInfo(name = SLOT)
        var slots: List<Int>,

        ) {

    override fun toString(): String {
        return "PokemonTypeMetaData(id=$id,  slots=$slots)"
    }

    companion object {

        const val TYPE_META_DATA_ID: String = "type_meta_data_id"
        const val SLOT: String = "slot"

        fun createTypeMetaDataId(pokemonId: Int, typeId: Int): Int {
            return "$pokemonId$uniqueTypeMetaDataConstant$typeId".toInt()
        }

        private const val uniqueTypeMetaDataConstant = 1001

        fun mapRemotePokemonToPokemonTypeMetaData(
                pokemonId: Int,
                typeSlots: List<ApiPokemonType>,
        ): PokemonTypeMetaData {
            return PokemonTypeMetaData(
                    id = createTypeMetaDataId(pokemonId, typeSlots.map { it.slot }.sum()),
                    slots = typeSlots.map { Pokemon.getPokemonIdFromUrl(it.type.url) },
            )
        }

    }

}

