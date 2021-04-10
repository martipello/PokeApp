package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.PokemonStat
import com.sealstudios.pokemonApp.database.`object`.BaseStats
import com.sealstudios.pokemonApp.database.`object`.joins.BaseStatsJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithBaseStats
import com.sealstudios.pokemonApp.database.dao.BaseStatsDao
import com.sealstudios.pokemonApp.database.dao.BaseStatsJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BaseStatsRepository @Inject constructor(
        private val baseStatsDao: BaseStatsDao,
        private val baseStatsJoinDao: BaseStatsJoinDao
) {

    private suspend fun insertPokemonBaseStats(baseStats: BaseStats) {
        baseStatsDao.insertPokemonBaseStats(baseStats)
    }

    suspend fun getPokemonWithStatsByIdAsync(pokemonId: Int): PokemonWithBaseStats {
        return baseStatsDao.getPokemonWithStatsByIdAsync(pokemonId)
    }

    //BASE STATS JOIN

    private suspend fun insertPokemonBaseStatsJoin(baseStatsJoin: BaseStatsJoin) {
        baseStatsJoinDao.insertPokemonBaseStatsJoin(baseStatsJoin)
    }

    suspend fun insertPokemonStats(
            stats: List<PokemonStat>,
            remotePokemonId: Int
    ) {
        withContext(Dispatchers.IO) {
            val pokemonBaseStats = BaseStats.mapRemoteStatToPokemonBaseStat(
                    pokemonId = remotePokemonId,
                    pokemonStats = stats
            )
            val pokemonBaseStatsJoin = BaseStatsJoin(remotePokemonId, pokemonBaseStats.id)
            insertPokemonBaseStats(pokemonBaseStats)
            insertPokemonBaseStatsJoin(pokemonBaseStatsJoin)
        }
    }

}