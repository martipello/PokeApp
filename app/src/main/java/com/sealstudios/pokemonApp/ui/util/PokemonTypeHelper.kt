package com.sealstudios.pokemonApp.ui.util

import android.annotation.SuppressLint
import com.sealstudios.pokemonApp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sealstudios.pokemonApp.database.`object`.PokemonType as pokemonType

enum class PokemonType(val color: Int, val icon: Int) {
//    NORMAL(color = R.color.normal, icon = R.drawable.ic_normal_type),
//    WATER(color = R.color.water, icon = R.drawable.ic_water_type),
//    FIRE(color = R.color.fire, icon = R.drawable.ic_fire_type),
//    GRASS(color = R.color.grass, icon = R.drawable.ic_grass_type),
//    ELECTRIC(color = R.color.electric, icon = R.drawable.ic_electric_type),
//    ICE(color = R.color.ice, icon = R.drawable.ic_ice_type),
//    FIGHTING(color = R.color.fighting, icon = R.drawable.ic_fighting_type),
//    POISON(color = R.color.poison, icon = R.drawable.ic_poison_type),
//    GROUND(color = R.color.ground, icon = R.drawable.ic_ground_type),
//    FLYING(color = R.color.flying, icon = R.drawable.ic_flying_type),
//    PSYCHIC(color = R.color.psychic, icon = R.drawable.ic_psychic_type),
//    BUG(color = R.color.bug, icon = R.drawable.ic_bug_type),
//    ROCK(color = R.color.rock, icon = R.drawable.ic_rock_type),
//    GHOST(color = R.color.ghost, icon = R.drawable.ic_ghost_type),
//    DARK(color = R.color.dark, icon = R.drawable.ic_dark_type),
//    DRAGON(color = R.color.dragon, icon = R.drawable.ic_dragon_type),
//    STEEL(color = R.color.steel, icon = R.drawable.ic_steel_type),
//    FAIRY(color = R.color.fairy, icon = R.drawable.ic_fairy_type),
//    UNKNOWN(color = R.color.white, icon = R.drawable.ic_pokeball);

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
    FAIRY(color = R.color.fairy, icon = R.drawable.fairy_type_icon),
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

        fun getAllPokemonTypes(): List<PokemonType> {
            return enumValues<PokemonType>().asList()
        }

        suspend fun getWeaknessWithMultipliers(types: List<pokemonType>): List<Pair<PokemonType, Double>> {
            return withContext(Dispatchers.Default) {

                fun addToMultiplier(typeName: String, doubleDamageFromMultipliersMap: MutableMap<String, Pair<PokemonType, Double>>) {
                    val typeEnum = getPokemonEnumTypeForPokemonType(typeName)
                    val entry = doubleDamageFromMultipliersMap.getOrPut(typeEnum.name, { Pair(typeEnum, 1.0) })
                    doubleDamageFromMultipliersMap[typeEnum.name] = Pair(entry.pokemonType, entry.weaknessResistanceMultiplier * 2)
                }

                val doubleDamageFromMultipliersMap = mutableMapOf<String, Pair<PokemonType, Double>>()

                types.forEach { type ->
                    type.doubleDamageFrom.forEach {
                        addToMultiplier(it, doubleDamageFromMultipliersMap)
                    }
                }

                return@withContext doubleDamageFromMultipliersMap.values.toList()

            }

        }

        suspend fun getResistanceWithMultipliers(types: List<pokemonType>): List<Pair<PokemonType, Double>> {

            return withContext(Dispatchers.Default) {
                fun minusFromMultiplier(typeName: String, resistanceMultipliersMap: MutableMap<String, Pair<PokemonType, Double>>) {
                    val typeEnum = getPokemonEnumTypeForPokemonType(typeName)
                    val entry = resistanceMultipliersMap.getOrPut(typeEnum.name, { Pair(typeEnum, 1.0) })
                    resistanceMultipliersMap[typeEnum.name] = Pair(entry.pokemonType, entry.weaknessResistanceMultiplier / 2)
                }

                val resistanceMultipliersMap = mutableMapOf<String, Pair<PokemonType, Double>>()

                types.forEach { type ->
                    type.halfDamageFrom.forEach {
                        minusFromMultiplier(it, resistanceMultipliersMap)
                    }
                }

                return@withContext resistanceMultipliersMap.values.toList()

            }

        }

        suspend fun getZeroMultipliers(types: List<pokemonType>): List<Pair<PokemonType, Double>> {

            return withContext(Dispatchers.Default){

                fun zeroMultiplier(typeName: String, zeroMultipliersMap: MutableMap<String, Pair<PokemonType, Double>>) {
                    val typeEnum = getPokemonEnumTypeForPokemonType(typeName)
                    zeroMultipliersMap[typeEnum.name] = Pair(typeEnum, 0.0)
                }

                val zeroMultipliersMap = mutableMapOf<String, Pair<PokemonType, Double>>()

                types.forEach { type ->
                    type.noDamageFrom.forEach {
                        zeroMultiplier(it, zeroMultipliersMap)
                    }
                }

                return@withContext zeroMultipliersMap.values.toList()
            }
        }
    }

}

val <A, B> Pair<A, B>.pokemonType: A get() = this.first
val <A, B> Pair<A, B>.weaknessResistanceMultiplier: B get() = this.second


