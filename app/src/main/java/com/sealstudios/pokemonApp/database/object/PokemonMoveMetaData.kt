package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sealstudios.pokemonApp.api.`object`.PokemonMoveVersion
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveLearning
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.NotNull
import java.util.*

@TypeConverters(RoomIntListConverter::class)
@Entity
open class PokemonMoveMetaData constructor(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = META_MOVE_ID)
    var id: Int = 0,

    @ColumnInfo(name = MOVE_NAME)
    var moveName: String = "",

    @ColumnInfo(name = VERSIONS_LEARNT)
    var versionsLearnt: List<String> = emptyList(),

    @ColumnInfo(name = LEVELS_LEARNED_AT)
    var levelsLearnedAt: List<Int> = emptyList(),

    @ColumnInfo(name = LEARN_METHODS)
    var learnMethods: List<String> = emptyList(),

    ) {

    override fun toString(): String {
        return "PokemonMoveMetaData(id=$id, moveName='$moveName', versionsLearnt=$versionsLearnt, levelsLearnedAt=$levelsLearnedAt, learnMethods=$learnMethods)"
    }

    companion object {

        const val META_MOVE_ID: String = "meta_move_id"
        const val MOVE_NAME: String = "meta_move_name"
        const val LEARN_METHODS: String = "learn_methods"
        const val LEVELS_LEARNED_AT: String = "levels_learned_at"
        const val VERSIONS_LEARNT: String = "versions_learnt"

        fun createMetaMoveId(pokemonId: Int, moveId: Int): Int {
            return "$pokemonId$uniqueMoveConstant$moveId".toInt()
        }

        fun mapRemotePokemonToMoveMetaData(
            pokemonMoveId: Int,
            pokemonId: Int,
            moveName: String,
            pokemonMoveVersions: List<PokemonMoveVersion>,
        ): PokemonMoveMetaData {

            return PokemonMoveMetaData(
                id = createMetaMoveId(pokemonId, pokemonMoveId),
                moveName = moveName,
                versionsLearnt = pokemonMoveVersions.map { it.version_group?.name ?: "" },
                levelsLearnedAt = pokemonMoveVersions.map { it.level_learned_at },
                learnMethods = pokemonMoveVersions.map { it.move_learn_method?.name ?: "" }
            )
        }

        suspend fun PokemonMoveMetaData.moveLearningList(): List<MoveLearning> {
            return withContext(Dispatchers.Default){
                val moveLearningList = mutableListOf<MoveLearning>()
                for (i in learnMethods.indices) {
                    moveLearningList.add(
                            MoveLearning(
                                    generation = versionsLearnt.getOrElse(index = i, defaultValue = { "" }),
                                    learntAt = levelsLearnedAt.getOrElse(index = i, defaultValue = { 0 }),
                                    learntBy = learnMethods[i]
                            )
                    )
                }
                return@withContext moveLearningList.toSet().toList()
            }
        }

        private const val uniqueMoveConstant = 101

    }

}

