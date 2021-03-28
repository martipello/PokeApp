package com.sealstudios.pokemonApp.database.`object`

import androidx.room.*
import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.api.`object`.NamedApiResource
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import com.sealstudios.pokemonApp.util.extensions.getIdFromUrl
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
            val id = apiPokemon.url.getIdFromUrl()
            val name = apiPokemon.name
            return Pokemon(
                    id = id,
                    name = name,
                    image = pokemonImage(id),
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
                    image = pokemonImage(apiPokemon.id),
                    height = apiPokemon.height ?: 0,
                    weight = apiPokemon.weight ?: 0,
                    sprite = apiPokemon.sprites?.front_default ?: "",
                    move_ids = apiPokemon.moves.map { it.move?.url?.getIdFromUrl() ?: -1 },
                    abilityIds = apiPokemon.abilities.map { pokemonAbility -> pokemonAbility.ability?.url?.getIdFromUrl() ?: -1 }
            )
        }

        fun pokemonImage(id: Int) =
                "https://firebasestorage.googleapis.com/v0/b/pokeapp-86eec.appspot.com/o/pokemon_image_$id.png?alt=media"

    }

    override fun toString(): String {
        return "Pokemon(id=$id, name='$name', image='$image', sprite=$sprite, weight=$weight, height=$height, move_ids=$move_ids, abilityIds=$abilityIds)"
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
