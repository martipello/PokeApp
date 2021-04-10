package com.sealstudios.pokemonApp.database.`object`

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sealstudios.pokemonApp.api.`object`.PokemonStat
import org.jetbrains.annotations.NotNull

@Entity
data class BaseStats(
        @NotNull
        @PrimaryKey
        @ColumnInfo(name = POKEMON_BASE_STAT_ID)
        var id: Int = 0,

        @ColumnInfo(name = POKEMON_BASE_STAT_HP)
        val hp: Int = 0,

        @ColumnInfo(name = POKEMON_BASE_STAT_ATTACK)
        val attack: Int = 0,

        @ColumnInfo(name = POKEMON_BASE_STAT_DEFENSE)
        val defense: Int = 0,

        @ColumnInfo(name = POKEMON_BASE_STAT_SPECIAL_ATTACK)
        val specialAttack: Int = 0,

        @ColumnInfo(name = POKEMON_BASE_STAT_SPECIAL_DEFENSE)
        val specialDefense: Int = 0,

        @ColumnInfo(name = POKEMON_BASE_STAT_SPEED)
        val speed: Int = 0,

        ) {

    companion object {
        const val POKEMON_BASE_STAT_ID: String = "pokemon_stat_id"
        const val POKEMON_BASE_STAT_HP: String = "pokemon_stat_hp"
        const val POKEMON_BASE_STAT_ATTACK: String = "pokemon_stat_attack"
        const val POKEMON_BASE_STAT_DEFENSE: String = "pokemon_stat_defense"
        const val POKEMON_BASE_STAT_SPECIAL_ATTACK: String = "pokemon_special_attack"
        const val POKEMON_BASE_STAT_SPECIAL_DEFENSE: String = "pokemon_special_defense"
        const val POKEMON_BASE_STAT_SPEED: String = "pokemon_stat_speed"

        private const val POKEMON_STAT_HP: String = "hp"
        private const val POKEMON_STAT_ATTACK: String = "attack"
        private const val POKEMON_STAT_DEFENSE: String = "defense"
        private const val POKEMON_STAT_SPECIAL_ATTACK: String = "special-attack"
        private const val POKEMON_STAT_SPECIAL_DEFENSE: String = "special-defense"
        private const val POKEMON_STAT_SPEED: String = "speed"


        private fun createBaseStatId(pokemonId: Int, baseStatTotal: Int): Int {
            return "$pokemonId$uniqueBaseStatConstant$baseStatTotal".toInt()
        }

        fun BaseStats.baseStatsTotal(): Int =  this.hp + this.attack + this.defense + this.specialAttack + this.specialDefense +  this.speed

        fun mapRemoteStatToPokemonBaseStat(
                pokemonId: Int,
                pokemonStats: List<PokemonStat>
        ): BaseStats {
            val total = pokemonStats.map { it.base_stat }.sum()
            return BaseStats(
                    id = createBaseStatId(pokemonId, total),
                    hp = getStatForName(pokemonStats, POKEMON_STAT_HP),
                    attack = getStatForName(pokemonStats, POKEMON_STAT_ATTACK),
                    defense = getStatForName(pokemonStats, POKEMON_STAT_DEFENSE),
                    specialAttack = getStatForName(pokemonStats, POKEMON_STAT_SPECIAL_ATTACK),
                    specialDefense = getStatForName(pokemonStats, POKEMON_STAT_SPECIAL_DEFENSE),
                    speed = getStatForName(pokemonStats, POKEMON_STAT_SPEED),
            )
        }

        private fun getStatForName(pokemonStats: List<PokemonStat>, statName: String): Int {
            return pokemonStats.filter { pokemonStat -> pokemonStat.stat?.name == statName }.map { it.base_stat }.getOrElse(0) { 0 }
        }

        private const val uniqueBaseStatConstant = 1001

    }


}