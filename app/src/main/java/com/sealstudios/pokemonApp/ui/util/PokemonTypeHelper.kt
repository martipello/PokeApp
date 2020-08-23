package com.sealstudios.pokemonApp.ui.util

import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonType as pokemonType

enum class PokemonType(val color: Int, val icon: Int) {
    NORMAL(color = R.color.normal, icon = R.drawable.normal_type_icon),
    WATER(color = R.color.water, icon = R.drawable.water_type_icon),
    FIRE(color = R.color.fire, icon = R.drawable.fire_type_icon),
    GRASS(color = R.color.grass, icon = R.drawable.grass_type_icon),
    ELECTRIC(color = R.color.electric, icon = R.drawable.lightning_type_icon),
    ICE(color = R.color.ice, icon = R.drawable.ice_type_icon),
    FIGHTING(color = R.color.fighting, icon = R.drawable.fighting_type_icon),
    POISON(color = R.color.poison, icon = R.drawable.poison_type_icon),
    GROUND(color = R.color.ground, icon = R.drawable.ground_type_icon),
    FLYING(color = R.color.flying, icon = R.drawable.flying_type_icon),
    PSYCHIC(color = R.color.psychic, icon = R.drawable.psychic_type_icon),
    BUG(color = R.color.bug, icon = R.drawable.bug_type_icon),
    ROCK(color = R.color.rock, icon = R.drawable.rock_type_icon),
    GHOST(color = R.color.ghost, icon = R.drawable.ghost_type_icon),
    DARK(color = R.color.dark, icon = R.drawable.dark_type_icon),
    DRAGON(color = R.color.dragon, icon = R.drawable.dragon_type_icon),
    STEEL(color = R.color.steel, icon = R.drawable.steel_type_icon),
    FAIRY(color = R.color.fairy, icon = R.drawable.fairy_type_icon);

    companion object {
        fun getPokemonEnumTypesForPokemonTypes(types: List<pokemonType>): List<PokemonType> {
            return types.map { valueOf(it.name.toUpperCase()) }
        }

        fun getAllPokemonTypesNames(): List<String> {
            return enumValues<PokemonType>().map { it.name.toLowerCase() }
        }

        fun getAllPokemonTypes(): List<PokemonType> {
            return enumValues<PokemonType>().asList()
        }

        fun initializePokemonTypeFilters(): MutableMap<String, Boolean> {
            val keys = getAllPokemonTypes()
            val allFilters = mutableMapOf<String, Boolean>()
            for (key in keys) {
                allFilters[key.name] = false
            }
            return allFilters
        }
    }

}


