package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.POKEMON_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.TYPE_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_ID, TYPE_ID])
class PokemonTypesJoin(
    @NotNull
    @ColumnInfo(name = POKEMON_ID, index = true)
    val pokemon_id: Int,

    @NotNull
    @ColumnInfo(name = TYPE_ID, index = true)
    val type_id: Int
) {
    companion object {

        fun mapTypeJoinsFromPokemonResponse(apiPokemon: com.sealstudios.pokemonApp.api.`object`.Pokemon): List<PokemonTypesJoin> {
            return apiPokemon.types.map { apiType ->
                PokemonTypesJoin(
                    pokemon_id = apiPokemon.id,
                    type_id = Pokemon.getPokemonIdFromUrl(apiType.type.url),
                )
            }
        }
    }
}