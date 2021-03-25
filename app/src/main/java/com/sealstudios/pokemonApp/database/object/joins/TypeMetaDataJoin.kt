package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.api.`object`.ApiPokemonType
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.TypeMetaData
import com.sealstudios.pokemonApp.database.`object`.TypeMetaData.Companion.TYPE_META_DATA_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, TYPE_META_DATA_ID])
class TypeMetaDataJoin(
        @NotNull
        @ColumnInfo(name = POKEMON_ID, index = true)
        val pokemon_id: Int,

        @NotNull
        @ColumnInfo(name = TYPE_META_DATA_ID, index = true)
        val type_meta_data_id: Int
) {
    companion object {

        fun mapTypeMetaDataJoinFromPokemonResponse(
                pokemonId: Int,
                typeSlots: List<ApiPokemonType>
        ): TypeMetaDataJoin {
            return TypeMetaDataJoin(
                    pokemon_id = pokemonId,
                    type_meta_data_id = TypeMetaData.createTypeMetaDataId(pokemonId, typeSlots.map { it.slot }.sum()),
            )
        }
    }
}