package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.api.`object`.Type
import com.sealstudios.pokemonApp.database.`object`.Pokemon
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

        fun mapTypeJoinFromTypeResponse(apiPokemonId: Int, type: Type): PokemonTypesJoin {
            return PokemonTypesJoin(
                    pokemon_id = apiPokemonId,
                    type_id = type.id,
            )
        }

        fun mapTypeJoinsFromPokemonResponse(apiPokemon: Int, apiTypeUrl: String): PokemonTypesJoin {
            return PokemonTypesJoin(
                    pokemon_id = apiPokemon,
                    type_id = Pokemon.getPokemonIdFromUrl(apiTypeUrl),
            )
        }

        fun mapTypeJoinsFromPokemonResponse(apiPokemon: ApiPokemon): List<PokemonTypesJoin> {
            return apiPokemon.types.map { apiType ->
                PokemonTypesJoin(
                    pokemon_id = apiPokemon.id,
                    type_id = Pokemon.getPokemonIdFromUrl(apiType.type.url),
                )
            }
        }
    }
}