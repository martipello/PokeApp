package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.POKEMON_TABLE_NAME
import com.sealstudios.pokemonApp.util.RoomStringListConverter
import org.jetbrains.annotations.NotNull
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon

@TypeConverters(RoomStringListConverter::class)
@Entity
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

    @ColumnInfo(name = POKEMON_SPECIES)
    var species: String,

    @ColumnInfo(name = POKEMON_MOVES)
    val moves: List<String>

) {
    companion object {

        const val POKEMON_ID: String = "pokemon_id"
        const val POKEMON_NAME: String = "pokemon_name"
        const val POKEMON_URL: String = "pokemon_url"
        const val POKEMON_HEIGHT: String = "pokemon_height"
        const val POKEMON_WEIGHT: String = "pokemon_weight"
        const val POKEMON_MOVES: String = "pokemon_moves"
        const val POKEMON_SPECIES: String = "pokemon_species"

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
                species = "Species",
                moves = apiPokemon.moves.map { it.move.name }
            )
        }

        fun buildDbPokemonFromPokemonResponse(apiPokemon: apiPokemon): Pokemon {
            return Pokemon(
                id = apiPokemon.id,
                name = apiPokemon.name,
                url = "https://pokeres.bastionbot.org/images/pokemon/${apiPokemon.id}.png",
                height = apiPokemon.height,
                weight = apiPokemon.weight,
                species = "Species",
                moves = apiPokemon.moves.map { it.move.name }
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
