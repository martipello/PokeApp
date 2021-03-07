package com.sealstudios.pokemonApp.database.`object`

import androidx.room.*
import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import org.jetbrains.annotations.NotNull

@TypeConverters(RoomIntListConverter::class)
@Entity(indices = [Index(value = [Pokemon.POKEMON_NAME])])
open class Pokemon constructor(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = POKEMON_ID)
    var id: Int = 0,

    @ColumnInfo(name = POKEMON_NAME)
    var name: String = "",

    @ColumnInfo(name = POKEMON_IMAGE)
    var image: String = "",

    @ColumnInfo(name = POKEMON_SPRITE)
    var sprite: String? = "",

    @ColumnInfo(name = POKEMON_WEIGHT)
    val weight: Int = 0,

    @ColumnInfo(name = POKEMON_HEIGHT)
    val height: Int = 0,

    @ColumnInfo(name = MOVE_IDS)
    var move_ids: List<Int> = emptyList(),

    @ColumnInfo(name = ABILITY_IDS)
    val abilityIds: List<Int> = emptyList(),

    ) {

    companion object {

        const val POKEMON_ID: String = "pokemon_id"
        const val POKEMON_NAME: String = "pokemon_name"
        const val POKEMON_IMAGE: String = "pokemon_image_url"
        const val POKEMON_HEIGHT: String = "pokemon_height"
        const val POKEMON_WEIGHT: String = "pokemon_weight"
        const val POKEMON_SPRITE: String = "pokemon_sprite"
        const val MOVE_IDS: String = "pokemon_move_id"
        const val ABILITY_IDS: String = "pokemon_ability_ids"

        fun defaultPokemon(apiPokemon: NamedApiResource): Pokemon {
            val id = getPokemonIdFromUrl(apiPokemon.url)
            return Pokemon(
                id = id,
                name = apiPokemon.name,
                image = highResPokemonUrl(id),
                height = 0,
                weight = 0,
                move_ids = listOf(),
                sprite = "",
                abilityIds = listOf()
            )
        }

        fun mapDbPokemonFromPokemonResponse(apiPokemon: ApiPokemon): Pokemon {

            return Pokemon(
                id = apiPokemon.id,
                name = apiPokemon.name ?: "",
                image = highResPokemonUrl(apiPokemon.id),
                height = apiPokemon.height ?: 0,
                weight = apiPokemon.weight ?: 0,
                sprite = apiPokemon.sprites?.front_default ?: "",
                move_ids = apiPokemon.moves.map { getPokemonIdFromUrl(it.move.url) },
                abilityIds = apiPokemon.abilities.map { pokemonAbility -> getPokemonIdFromUrl(pokemonAbility.ability.url) }
            )
        }

        fun highResPokemonUrl(pokemonId: Int) =
            "https://pokeres.bastionbot.org/images/pokemon/${pokemonId}.png"

        fun getPokemonIdFromUrl(pokemonUrl: String?): Int {
            if (pokemonUrl != null) {
                val pokemonIndex = pokemonUrl.split('/')
                if (pokemonIndex.size >= 2) {
                    return pokemonIndex[pokemonIndex.size - 2].toInt()
                }
            }
            return -1
        }

    }
}

fun Pokemon.isDefault(): Boolean {
    val defaultPokemon = Pokemon.defaultPokemon(
        NamedApiResource(
            name = this.name,
            id = this.id,
            category = "",
            url = "https://pokeapi.co/api/v2/pokemon/${this.id}/"
        )
    )
    return this.id == defaultPokemon.id
            && this.name == defaultPokemon.name
            && this.height == defaultPokemon.height
            && this.weight == defaultPokemon.weight
            && this.image == defaultPokemon.image
            && this.sprite == defaultPokemon.sprite
            && this.move_ids == defaultPokemon.move_ids
}
