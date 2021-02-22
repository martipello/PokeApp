package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonBaseStatsJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithBaseStats
import com.sealstudios.pokemonApp.database.dao.PokemonBaseStatsDao
import com.sealstudios.pokemonApp.database.dao.PokemonBaseStatsJoinDao
import javax.inject.Inject


class PokemonBaseStatsRepository @Inject constructor(
        private val pokemonBaseStatsDao: PokemonBaseStatsDao,
        private val pokemonBaseStatsJoinDao: PokemonBaseStatsJoinDao
) {

    suspend fun insertPokemonBaseStats(pokemonBaseStats: PokemonBaseStats) {
        pokemonBaseStatsDao.insertPokemonBaseStats(pokemonBaseStats)
    }

    suspend fun getPokemonWithStatsByIdAsync(pokemonId: Int): PokemonWithBaseStats {
        return pokemonBaseStatsDao.getPokemonWithStatsByIdAsync(pokemonId)
    }

    //BASE STATS JOIN

    suspend fun insertPokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin) {
        pokemonBaseStatsJoinDao.insertPokemonBaseStatsJoin(pokemonBaseStatsJoin)
    }

}