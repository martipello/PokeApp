package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.api.`object`.Type
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.Type.Companion.TYPE_ID
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, TYPE_ID])
class TypesJoin(
        @NotNull
        @ColumnInfo(name = POKEMON_ID, index = true)
        val pokemon_id: Int,

        @NotNull
        @ColumnInfo(name = TYPE_ID, index = true)
        val type_id: Int
) {
    companion object {

        fun mapTypeJoinFromTypeResponse(apiPokemonId: Int, type: Type): TypesJoin {
            return TypesJoin(
                    pokemon_id = apiPokemonId,
                    type_id = type.id,
            )
        }

        fun mapTypeJoinsFromPokemonResponse(apiPokemon: Int, apiTypeUrl: String?): TypesJoin {
            return TypesJoin(
                    pokemon_id = apiPokemon,
                    type_id = apiTypeUrl?.getIdFromUrl() ?: -1,
            )
        }

    }
}