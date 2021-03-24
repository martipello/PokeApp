package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.ApiPokemonMove
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveTypeOrCategory
import com.sealstudios.pokemonApp.ui.util.PokemonCategory
import com.sealstudios.pokemonApp.ui.util.PokemonType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonMove @JvmOverloads constructor(

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = MOVE_ID)
    var id: Int,

    @ColumnInfo(name = MOVE_NAME)
    var name: String,

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

    @ColumnInfo(name = DESCRIPTION)
    var description: String = "",


    ) {

    override fun toString(): String {
        return "PokemonMove(id=$id, name='$name', description=$description, type=$type, generation=$generation)"
    }

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
        const val DESCRIPTION: String = "description"

        fun mapRemotePokemonMoveToDatabasePokemonMove(
            apiPokemonMove: ApiPokemonMove
        ): PokemonMove {
            return PokemonMove(
                id = apiPokemonMove.id,
                name = apiPokemonMove.name ?: "",
                accuracy = apiPokemonMove.accuracy ?: 0,
                description = apiPokemonMove.flavor_text_entries?.findLast { it.language?.name == "en" }?.flavor_text
                    ?: "",
                pp = apiPokemonMove.pp ?: 0,
                priority = apiPokemonMove.priority,
                power = apiPokemonMove.power ?: 0,
                generation = apiPokemonMove.generation?.name ?: "",
                damage_class = apiPokemonMove.damage_class?.name ?: "",
                type = apiPokemonMove.type?.name ?: "",
                damage_class_effect_chance = apiPokemonMove.effect_chance,
            )
        }

        suspend fun PokemonMove.typeOrCategoryList(): List<PokemonMoveTypeOrCategory> {
            return withContext(context = Dispatchers.Default) {
                val typesOrCategoriesList = mutableListOf<PokemonMoveTypeOrCategory>()
                val type = PokemonType.getPokemonEnumTypeForPokemonType(type)
                typesOrCategoriesList.add(
                        PokemonMoveTypeOrCategory(
                                type = type,
                                category = null,
                                itemType = PokemonType.itemType
                        )
                )
                val category = PokemonCategory.getCategoryForDamageClass(damage_class)
                typesOrCategoriesList.add(
                        PokemonMoveTypeOrCategory(
                                type = null,
                                category = category,
                                itemType = PokemonCategory.itemType
                        )
                )
                return@withContext typesOrCategoriesList
            }
        }


    }
}
