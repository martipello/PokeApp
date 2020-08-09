package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.PokemonSpecies
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
    val height: Int,

    @ColumnInfo(name = POKEMON_FORM)
    var form: String,

    @ColumnInfo(name = POKEMON_MOVES)
    val moves: List<String>,

    @ColumnInfo(name = POKEMON_TYPES)
    val types: List<String>

) {
    companion object {

        const val POKEMON_ID: String = "id"
        const val POKEMON_NAME: String = "name"
        const val POKEMON_URL: String = "url"
        const val POKEMON_HEIGHT: String = "height"
        const val POKEMON_WEIGHT: String = "weight"
        const val POKEMON_MOVES: String = "moves"
        const val POKEMON_TYPES: String = "types"
        const val POKEMON_FORM: String = "form"

        fun mapRemotePokemonToDatabasePokemon(
            dbPokemon: Pokemon,
            apiPokemon: apiPokemon
        ): Pokemon {
            return Pokemon(
                id = dbPokemon.id,
                url = dbPokemon.url,
                name = apiPokemon.name,
                height = apiPokemon.height,
                weight = apiPokemon.weight,
                form = apiPokemon.forms.map { it.name }.first(),
                moves = apiPokemon.moves.map { it.move.name },
                types = apiPokemon.types.map { it.type.name }
            )
        }

        fun buildDbPokemonFromPokemonResponse(apiPokemon: apiPokemon): Pokemon {
            return Pokemon(
                id = apiPokemon.id,
                name = apiPokemon.name,
                url = "https://pokeres.bastionbot.org/images/pokemon/${apiPokemon.id}.png",
                weight = 0,
                height = 0,
                form = apiPokemon.forms.map { it.name }.first(),
                moves = apiPokemon.moves.map { it.move.name },
                types = apiPokemon.types.map { it.type.name }
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
