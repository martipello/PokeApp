package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonMove(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = MOVE_ID)
    var id: Int,

    @ColumnInfo(name = MOVE_NAME)
    var name: String,

    @ColumnInfo(name = MOVE_ACCURACY)
    var accuracy: Int,

    @ColumnInfo(name = MOVE_PP)
    var pp: Int,

    @ColumnInfo(name = MOVE_PRIORITY)
    var priority: Int,

    @ColumnInfo(name = MOVE_POWER)
    var power: Int,

    @ColumnInfo(name = MOVE_DAMAGE_CLASS_NAME)
    var damage_class: Int,

    @ColumnInfo(name = MOVE_DAMAGE_CLASS_EFFECT_CHANCE)
    var damage_class_effect_chance: Int,

    @ColumnInfo(name = MOVE_GENERATION)
    var generation: String,

    @ColumnInfo(name = MOVE_TYPE)
    var type: String
//effect_chance
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

    }
}
