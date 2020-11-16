package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonType as pokemonType

enum class PokemonType(val color: Int, val icon: Int) {
    NORMAL(color = R.color.normal, icon = R.drawable.ic_normal_type),
    WATER(color = R.color.water, icon = R.drawable.ic_water_type),
    FIRE(color = R.color.fire, icon = R.drawable.ic_fire_type),
    GRASS(color = R.color.grass, icon = R.drawable.ic_grass_type),
    ELECTRIC(color = R.color.electric, icon = R.drawable.ic_electric_type),
    ICE(color = R.color.ice, icon = R.drawable.ic_ice_type),
    FIGHTING(color = R.color.fighting, icon = R.drawable.ic_fighting_type),
    POISON(color = R.color.poison, icon = R.drawable.ic_poison_type),
    GROUND(color = R.color.ground, icon = R.drawable.ic_ground_type),
    FLYING(color = R.color.flying, icon = R.drawable.ic_flying_type),
    PSYCHIC(color = R.color.psychic, icon = R.drawable.ic_psychic_type),
    BUG(color = R.color.bug, icon = R.drawable.ic_bug_type),
    ROCK(color = R.color.rock, icon = R.drawable.ic_rock_type),
    GHOST(color = R.color.ghost, icon = R.drawable.ic_ghost_type),
    DARK(color = R.color.dark, icon = R.drawable.ic_dark_type),
    DRAGON(color = R.color.dragon, icon = R.drawable.ic_dragon_type),
    STEEL(color = R.color.steel, icon = R.drawable.ic_steel_type),
    FAIRY(color = R.color.fairy, icon = R.drawable.ic_fairy_type),
    UNKNOWN(color = R.color.white, icon = R.drawable.ic_pokeball);

    companion object {

        const val itemType = 1002

        @SuppressLint("DefaultLocale")
        fun getPokemonEnumTypesForPokemonTypes(types: List<pokemonType>): List<PokemonType> {
            return types.map {
                try {
                    valueOf(it.name.toUpperCase())
                } catch (e: Exception) {
                    UNKNOWN
                }
            }
        }

        @SuppressLint("DefaultLocale")
        fun getPokemonEnumTypeForPokemonType(type: String): PokemonType {
            return try {
                valueOf(type.toUpperCase())
            } catch (e: Exception) {
                UNKNOWN
            }
        }

        @SuppressLint("DefaultLocale")
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

        @SuppressLint("DefaultLocale")
        fun createPokemonTypeChip(pokemonType: PokemonType, context: Context): Chip? {
            val chip =
                LayoutInflater.from(context).inflate(R.layout.pokemon_type_chip, null) as Chip
            chip.text = pokemonType.name.capitalize()
            chip.chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
            chip.setChipBackgroundColorResource(pokemonType.color)
            chip.isCheckable = false
            chip.isClickable = false
            chip.rippleColor = null
            return chip
        }

        @SuppressLint("DefaultLocale", "InflateParams")
        fun setPokemonTypeChip(pokemonType: PokemonType, context: Context, chip: Chip) {
            chip.text = pokemonType.name.capitalize()
            chip.chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
            chip.setChipBackgroundColorResource(pokemonType.color)
            chip.isCheckable = false
            chip.isClickable = false
            chip.rippleColor = null
            chip.visibility = View.VISIBLE
        }

    }

}


