package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.TABLE_NAME
import org.jetbrains.annotations.NotNull
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon

@Entity(tableName = TABLE_NAME)
data class Pokemon(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = POKEMON_ID)
    var id: Int,

    @ColumnInfo(name = POKEMON_NAME)
    var name: String,

    @ColumnInfo(name = POKEMON_URL)
    var url: String,

    @ColumnInfo(name = POKEMON_WEIGHT)
    val weight: Int,

    @ColumnInfo(name = POKEMON_HEIGHT)
    val height: Int

    ) {
    companion object {

        const val POKEMON_ID: String = "id"
        const val POKEMON_NAME: String = "name"
        const val POKEMON_URL: String = "url"
        const val POKEMON_HEIGHT: String = "height"
        const val MOVES: String = "moves"
        const val POKEMON_WEIGHT: String = "weight"

        fun mapRemotePokemonToDatabasePokemon(
            dbPokemon: Pokemon,
            apiPokemon: apiPokemon
        ): Pokemon {
            return Pokemon(
                id = dbPokemon.id,
                name = apiPokemon.name,
                height = apiPokemon.height,
                weight = apiPokemon.weight,
                url = dbPokemon.url
            )
        }

        fun buildDbPokemonFromPokemonResponse(pokemon: apiPokemon): Pokemon {
            return Pokemon(
                id = pokemon.id,
                name = pokemon.name,
                url = "https://pokeres.bastionbot.org/images/pokemon/${pokemon.id}.png",
                weight = 0,
                height = 0
            )
        }

        fun getPokemonIdFromUrl(pokemonUrl: String): Int {
            val pokemonIndex = pokemonUrl.split("/".toRegex()).toTypedArray()
            return if (pokemonIndex.size >= 2) {
                pokemonIndex[pokemonIndex.size - 2].toInt()
            } else {
                -1
            }
        }

    }
}
