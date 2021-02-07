package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStatsJoin
import com.sealstudios.pokemonApp.database.`object`.PokemonWithBaseStats
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

    suspend fun insertPokemonBaseStats(pokemonBaseStats: List<PokemonBaseStats>) {
        pokemonBaseStatsDao.insertPokemonBaseStatsList(pokemonBaseStats)
    }

    suspend fun updatePokemonBaseStats(pokemonBaseStats: PokemonBaseStats) {
        pokemonBaseStatsDao.updatePokemonBaseStats(pokemonBaseStats)
    }

    suspend fun getPokemonWithStatsByIdAsync(pokemonId: Int): PokemonWithBaseStats {
        return pokemonBaseStatsDao.getPokemonWithStatsByIdAsync(pokemonId)
    }

    suspend fun deletePokemonBaseStats(pokemonBaseStats: PokemonBaseStats) {
        pokemonBaseStatsDao.deletePokemonBaseStats(pokemonBaseStats)
    }

    //BASE STATS JOIN

    suspend fun insertPokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin) {
        pokemonBaseStatsJoinDao.insertPokemonBaseStatsJoin(pokemonBaseStatsJoin)
    }

    suspend fun insertPokemonBaseStatsJoins(pokemonBaseStatsJoin: List<PokemonBaseStatsJoin>) {
        pokemonBaseStatsJoinDao.insertPokemonBaseStatsJoinsList(pokemonBaseStatsJoin)
    }

    suspend fun updatePokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin) {
        pokemonBaseStatsJoinDao.updatePokemonBaseStatsJoin(pokemonBaseStatsJoin)
    }

    suspend fun deletePokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin) {
        pokemonBaseStatsJoinDao.deletePokemonBaseStatsJoin(pokemonBaseStatsJoin)
    }

}