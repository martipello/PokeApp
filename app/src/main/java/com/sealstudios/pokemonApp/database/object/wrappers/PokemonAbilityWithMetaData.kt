package com.sealstudios.pokemonApp.database.`object`.wrappers

import com.sealstudios.pokemonApp.database.`object`.PokemonAbility
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData

data class PokemonAbilityWithMetaData(
        val pokemonAbility: PokemonAbility,
        val pokemonAbilityMetaData: PokemonAbilityMetaData
) {

    companion object
}