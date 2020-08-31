package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import com.sealstudios.pokemonApp.api.`object`.Pokemon as apiPokemon

@Entity
data class Pokemon(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = POKEMON_ID)
    var id: Int,

    @ColumnInfo(name = POKEMON_NAME)
    var name: String,

    @ColumnInfo(name = POKEMON_IMAGE)
    var image: String,

    @ColumnInfo(name = POKEMON_SPRITE)
    var sprite: String?,

    @ColumnInfo(name = POKEMON_WEIGHT)
    val weight: Int,

    @ColumnInfo(name = POKEMON_HEIGHT)
    val height: Int

) {
    companion object {

        const val POKEMON_ID: String = "pokemon_id"
        const val POKEMON_NAME: String = "pokemon_name"
        const val POKEMON_IMAGE: String = "pokemon_image_url"
        const val POKEMON_HEIGHT: String = "pokemon_height"
        const val POKEMON_WEIGHT: String = "pokemon_weight"
        const val POKEMON_SPRITE: String = "pokemon_sprite"

        fun mapDbPokemonFromPokemonResponse(apiPokemon: apiPokemon): Pokemon {
            return Pokemon(
                id = apiPokemon.id,
                name = apiPokemon.name,
                image = "https://pokeres.bastionbot.org/images/pokemon/${apiPokemon.id}.png",
                height = apiPokemon.height,
                weight = apiPokemon.weight,
                sprite = apiPokemon.sprites.front_default
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
