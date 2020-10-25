package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.getPokemonIdFromUrl
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonMove(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = MOVE_ID)
    var id: Int,

    @ColumnInfo(name = MOVE_NAME)
    var name: String,

    @ColumnInfo(name = VERSIONS_LEARNT)
    var versionsLearnt: List<String> = emptyList(),

    @ColumnInfo(name = LEVELS_LEARNED_AT)
    var levelsLearnedAt: List<Int> = emptyList(),

    @ColumnInfo(name = LEARN_METHODS)
    var learnMethods: List<String> = emptyList(),

    @ColumnInfo(name = MOVE_ACCURACY)
    var accuracy: Int = 0,

    @ColumnInfo(name = MOVE_PP)
    var pp: Int = 0,

    @ColumnInfo(name = MOVE_PRIORITY)
    var priority: Int = 0,

    @ColumnInfo(name = MOVE_POWER)
    var power: Int = 0,

    @ColumnInfo(name = MOVE_DAMAGE_CLASS_NAME)
    var damage_class: String = "",

    @ColumnInfo(name = MOVE_DAMAGE_CLASS_EFFECT_CHANCE)
    var damage_class_effect_chance: Int = 0,

    @ColumnInfo(name = MOVE_GENERATION)
    var generation: String = "",

    @ColumnInfo(name = MOVE_TYPE)
    var type: String = "",

    ) {

    companion object {

        const val MOVE_ID: String = "move_id"
        const val MOVE_NAME: String = "move_name"
        const val MOVE_ACCURACY: String = "move_accuracy"
        const val MOVE_PP: String = "move_pp"
        const val MOVE_PRIORITY: String = "move_priority"
        const val MOVE_POWER: String = "move_power"
        const val MOVE_DAMAGE_CLASS_NAME: String = "move_damage_class_name"
        const val MOVE_DAMAGE_CLASS_EFFECT_CHANCE: String = "move_damage_class_effect_chance"
        const val MOVE_GENERATION: String = "move_generation"
        const val MOVE_TYPE: String = "move_type"
        const val LEARN_METHODS: String = "learn_methods"
        const val LEVELS_LEARNED_AT: String = "levels_learned_at"
        const val VERSIONS_LEARNT: String = "versions_learnt"

        fun mapRemotePokemonMoveToDatabasePokemonMove(
            apiPokemonMove: com.sealstudios.pokemonApp.api.`object`.PokemonMove
        ): PokemonMove {
            return PokemonMove(
                id = apiPokemonMove.id,
                name = apiPokemonMove.name,
                accuracy = apiPokemonMove.accuracy ?: 0,
                pp = apiPokemonMove.pp,
                priority = apiPokemonMove.priority,
                power = apiPokemonMove.power ?: 0,
                generation = apiPokemonMove.generation.name,
                damage_class = apiPokemonMove.damage_class.name,
                type = apiPokemonMove.type.name,
                damage_class_effect_chance = apiPokemonMove.effect_chance
            )
        }

        fun mapPartialRemotePokemonMoveToDatabasePokemonMove(
            apiPokemonMoveResponse: com.sealstudios.pokemonApp.api.`object`.PokemonMoveResponse
        ): PokemonMove {
            return PokemonMove(
                id = getPokemonIdFromUrl(apiPokemonMoveResponse.move.url),
                name = apiPokemonMoveResponse.move.name,
                levelsLearnedAt = apiPokemonMoveResponse.version_group_details.map { it.level_learned_at },
                learnMethods = apiPokemonMoveResponse.version_group_details.map { it.move_learn_method.name },
                versionsLearnt = apiPokemonMoveResponse.version_group_details.map { it.version_group.name }
            )
        }
    }
}
