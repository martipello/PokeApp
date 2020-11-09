package com.sealstudios.pokemonApp.database.`object`

import androidx.room.*
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import org.jetbrains.annotations.NotNull
import com.sealstudios.pokemonApp.api.`object`.Pokemon as ApiPokemon

@TypeConverters(RoomIntListConverter::class)
@Entity(indices = [Index(value = [Pokemon.POKEMON_NAME])])
data class Pokemon constructor(
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
    val height: Int,

    @ColumnInfo(name = MOVE_IDS)
    var move_ids: List<Int>,

    @ColumnInfo(name = VERSIONS_LEARNT)
    var versionsLearnt: List<String> = emptyList(),

    @ColumnInfo(name = LEVELS_LEARNED_AT)
    var levelsLearnedAt: List<Int> = emptyList(),

    @ColumnInfo(name = LEARN_METHODS)
    var learnMethods: List<String> = emptyList(),


    ) {

    companion object {

        const val POKEMON_ID: String = "pokemon_id"
        const val POKEMON_NAME: String = "pokemon_name"
        const val POKEMON_IMAGE: String = "pokemon_image_url"
        const val POKEMON_HEIGHT: String = "pokemon_height"
        const val POKEMON_WEIGHT: String = "pokemon_weight"
        const val POKEMON_SPRITE: String = "pokemon_sprite"
        const val MOVE_IDS: String = "pokemon_move_id"
        const val LEARN_METHODS: String = "learn_methods"
        const val LEVELS_LEARNED_AT: String = "levels_learned_at"
        const val VERSIONS_LEARNT: String = "versions_learnt"

        fun mapDbPokemonFromPokemonResponse(apiPokemon: ApiPokemon): Pokemon {

            val moveVersions = apiPokemon.moves.map { it.version_group_details }.flatten()

            return Pokemon(
                id = apiPokemon.id,
                name = apiPokemon.name,
                image = highResPokemonUrl(apiPokemon.id),
                height = apiPokemon.height,
                weight = apiPokemon.weight,
                sprite = apiPokemon.sprites.front_default,
                move_ids = apiPokemon.moves.map { getPokemonIdFromUrl(it.move.url) },
                levelsLearnedAt = moveVersions.map { it.level_learned_at },
                learnMethods =  moveVersions.map { it.move_learn_method.name },
                versionsLearnt = moveVersions.map { it.version_group.name }
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
