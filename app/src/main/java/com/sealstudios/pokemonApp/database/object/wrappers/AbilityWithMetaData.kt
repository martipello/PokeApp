package com.sealstudios.pokemonApp.database.`object`.wrappers

import com.sealstudios.pokemonApp.database.`object`.Ability
import com.sealstudios.pokemonApp.database.`object`.AbilityMetaData

data class AbilityWithMetaData(
        val ability: Ability,
        val abilityMetaData: AbilityMetaData
) {

    companion object
}