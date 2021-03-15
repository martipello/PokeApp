package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.Ability
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.util.extensions.capitalize
import org.jetbrains.annotations.NotNull

@Entity
data class PokemonAbility @JvmOverloads constructor(

        @NotNull
        @PrimaryKey
        @ColumnInfo(name = ABILITY_ID)
        var id: Int,

        @ColumnInfo(name = ABILITY_NAME)
        var name: String = "",

        @ColumnInfo(name = ABILITY_GENERATION)
        var generation: String = "",

        @ColumnInfo(name = ABILITY_EFFECT_CHANGE)
        var abilityEffectChange: String = "",

        @ColumnInfo(name = ABILITY_EFFECT_CHANGE_VERSION_GROUP)
        var abilityEffectChangeVersionGroup: String = "",

        @ColumnInfo(name = ABILITY_EFFECT_ENTRY)
        var abilityEffectEntry: String = "",

        @ColumnInfo(name = ABILITY_EFFECT_ENTRY_SHORT_EFFECT)
        var abilityEffectEntryShortEffect: String = "",

        @ColumnInfo(name = ABILITY_FLAVOR_TEXT)
        var flavorText: String = "",

        @ColumnInfo(name = ABILITY_IS_MAIN_SERIES)
        var isMainSeries: Boolean = false,

        ) {

    override fun toString(): String {
        return "PokemonAbility(id=$id, name='$name', generation='$generation', effectChange='$abilityEffectChange', abilityEffectChangeVersionGroup='$abilityEffectChangeVersionGroup', abilityEffectEntry='$abilityEffectEntry', isMainSeries=$isMainSeries)"
    }

    companion object {
        fun mapRemotePokemonAbilityToDatabasePokemonAbility(ability: Ability): PokemonAbility {
            return PokemonAbility(
                    id = ability.id,
                    name = ability.name ?: "",
                    generation = ability.generation?.name ?: "",
                    abilityEffectChange = ability.effect_changes?.map { abilityEffectChange ->
                        abilityEffectChange.effect_entries
                                ?.firstOrNull { effect -> effect.language?.name == "en" }
                    }
                            ?.map { it?.effect }?.getOrElse(0) { "" } ?: "",
                    abilityEffectChangeVersionGroup = PokemonGeneration.getVersionForGeneration(
                            PokemonGeneration.getGeneration(
                                    ability.generation?.name ?: ""
                            )
                    ),
                    abilityEffectEntry = ability.effect_entries?.firstOrNull { effect -> effect.language.name == "en" }?.effect
                            ?: "",
                    abilityEffectEntryShortEffect = ability.effect_entries?.firstOrNull { effect -> effect.language.name == "en" }?.short_effect
                            ?: "",
                    flavorText = ability.flavor_text_entries?.firstOrNull { abilityFlavorText -> abilityFlavorText.language.name == "en" }?.flavor_text
                            ?: "",
                    isMainSeries = ability.is_main_series ?: false
            )
        }

        const val ABILITY_ID: String = "ability_id"
        const val ABILITY_NAME: String = "ability_name"
        const val ABILITY_GENERATION: String = "ability_generation"
        const val ABILITY_EFFECT_CHANGE: String = "ability_effect_change"
        const val ABILITY_EFFECT_CHANGE_VERSION_GROUP: String = "ability_effect_change_version_group"
        const val ABILITY_EFFECT_ENTRY: String = "ability_effect_entry"
        const val ABILITY_FLAVOR_TEXT: String = "ability_flavor_text"
        const val ABILITY_EFFECT_ENTRY_SHORT_EFFECT: String = "ability_effect_entry_short_effect"
        const val ABILITY_IS_MAIN_SERIES: String = "ability_is_main_series"


        fun getPokemonAbilityIdFromUrl(pokemonUrl: String?): Int {
            if (pokemonUrl != null) {
                val pokemonIndex = pokemonUrl.split('/')
                if (pokemonIndex.size >= 2) {
                    return pokemonIndex[pokemonIndex.size - 2].toInt()
                }
            }
            return -1
        }

        fun formatAbilityName(abilityName: String): String {
            val parts = abilityName.split(regex = Regex("-"), limit = 2)
            return if (parts.size > 1) {
                val stringBuilder = StringBuilder()
                for (part in parts) {
                    stringBuilder.append(part.capitalize())
                    stringBuilder.append(" ")
                }
                stringBuilder.toString()
            } else {
                abilityName.capitalize()
            }
        }


    }

}
