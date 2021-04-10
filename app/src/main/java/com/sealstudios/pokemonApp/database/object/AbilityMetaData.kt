package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import org.jetbrains.annotations.NotNull

@TypeConverters(RoomIntListConverter::class)
@Entity
open class AbilityMetaData constructor(
    @NotNull
    @PrimaryKey
    @ColumnInfo(name = ABILITY_META_DATA_ID)
    var id: Int = 0,

    @ColumnInfo(name = ABILITY_META_DATA_NAME)
    var abilityName: String = "",

    @ColumnInfo(name = IS_HIDDEN)
    var isHidden: Boolean = false,

    ) {

    override fun toString(): String {
        return "PokemonAbilityMetaData(id=$id, abilityName='$abilityName', isHidden=$isHidden)"
    }

    companion object {

        const val ABILITY_META_DATA_ID: String = "ability_meta_data_id"
        const val ABILITY_META_DATA_NAME: String = "ability_meta_data_name"
        const val IS_HIDDEN: String = "is_hidden"

        fun createAbilityMetaDataId(pokemonId: Int, abilityId: Int): Int {
            return "$pokemonId$uniqueAbilityMetaDataConstant$abilityId".toInt()
        }

        private const val uniqueAbilityMetaDataConstant = 1001

        fun mapRemotePokemonToAbilityMetaData(
            pokemonAbilityId: Int,
            pokemonId: Int,
            abilityName: String,
            isHidden: Boolean,
        ): AbilityMetaData {

            return AbilityMetaData(
                id = createAbilityMetaDataId(pokemonId, pokemonAbilityId),
                abilityName = abilityName,
                isHidden = isHidden,
            )
        }

    }

}

