package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.Ability
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
                abilityEffectChange = ability.effectChanges?.map { abilityEffectChange ->
                    abilityEffectChange.effectEntries
                        .firstOrNull { effect -> effect.language.name == "en" }
                }
                    ?.map { it?.effect }?.getOrElse(0) { "" } ?: "",
                abilityEffectChangeVersionGroup = ability.effectChanges?.map { abilityEffectChange -> abilityEffectChange.versionGroup.name }
                    ?.getOrElse(0) { "" } ?: "",
                abilityEffectEntry = ability.effectEntries?.firstOrNull { effect -> effect.language.name == "en" }?.effect
                    ?: "",
                abilityEffectEntryShortEffect = ability.effectEntries?.firstOrNull { effect -> effect.language.name == "en" }?.shortEffect
                    ?: "",
                flavorText = ability.flavorTextEntries?.firstOrNull { abilityFlavorText -> abilityFlavorText.language.name == "en" }?.flavorText ?: "",
                isMainSeries = ability.isMainSeries ?: false
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

    }

}
