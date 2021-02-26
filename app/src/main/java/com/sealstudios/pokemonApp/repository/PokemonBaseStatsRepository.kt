package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.PokemonStat
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonBaseStatsJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithBaseStats
import com.sealstudios.pokemonApp.database.dao.PokemonBaseStatsDao
import com.sealstudios.pokemonApp.database.dao.PokemonBaseStatsJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonBaseStatsRepository @Inject constructor(
        private val pokemonBaseStatsDao: PokemonBaseStatsDao,
        private val pokemonBaseStatsJoinDao: PokemonBaseStatsJoinDao
) {

    private suspend fun insertPokemonBaseStats(pokemonBaseStats: PokemonBaseStats) {
        pokemonBaseStatsDao.insertPokemonBaseStats(pokemonBaseStats)
    }

    suspend fun getPokemonWithStatsByIdAsync(pokemonId: Int): PokemonWithBaseStats {
        return pokemonBaseStatsDao.getPokemonWithStatsByIdAsync(pokemonId)
    }

    //BASE STATS JOIN

    private suspend fun insertPokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin) {
        pokemonBaseStatsJoinDao.insertPokemonBaseStatsJoin(pokemonBaseStatsJoin)
    }

    suspend fun insertPokemonStats(
            stats: List<PokemonStat>,
            remotePokemonId: Int
    ) {
        withContext(Dispatchers.IO) {
            val pokemonBaseStats = PokemonBaseStats.mapRemoteStatToPokemonBaseStat(
                    pokemonId = remotePokemonId,
                    pokemonStats = stats
            )
            val pokemonBaseStatsJoin = PokemonBaseStatsJoin(remotePokemonId, pokemonBaseStats.id)
            insertPokemonBaseStats(pokemonBaseStats)
            insertPokemonBaseStatsJoin(pokemonBaseStatsJoin)
        }
    }

}