package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.EvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionChain.Companion.mapToEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail.Companion.getEvolutionDetailListForEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.joins.EvolutionDetailJoin
import com.sealstudios.pokemonApp.database.`object`.relations.EvolutionChainWithDetailList
import com.sealstudios.pokemonApp.database.dao.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class EvolutionRepository @Inject constructor(
        private val evolutionChainDao: EvolutionChainDao,
        private val evolutionDetailDao: EvolutionDetailDao,
        private val evolutionDetailJoinDao: EvolutionDetailJoinDao,
) {

    suspend fun getPokemonEvolutionChainWithDetailListByIdAsync(id: Int): EvolutionChainWithDetailList? {
        return withContext(Dispatchers.IO) {
            return@withContext evolutionChainDao.getPokemonEvolutionChainWithDetailListById(id)
        }
    }

    private suspend fun insertPokemonEvolutionChain(evolutionChain: EvolutionChain) {
        evolutionChainDao.insertPokemonEvolutionChain(evolutionChain)
    }

    private suspend fun insertPokemonEvolutionDetail(evolutionDetail: EvolutionDetail) {
        evolutionDetailDao.insertPokemonEvolutionDetail(evolutionDetail)
    }

    private suspend fun insertPokemonEvolutionDetailJoin(evolutionDetailJoin: EvolutionDetailJoin) {
        evolutionDetailJoinDao.insertPokemonEvolutionDetailJoin(evolutionDetailJoin)
    }

    suspend fun insertPokemonEvolution(
            evolutionChain: com.sealstudios.pokemonApp.api.`object`.EvolutionChain
    ) {
        withContext(Dispatchers.IO) {
            val pokemonEvolutionChain = mapToEvolutionChain(evolutionChain)
            val pokemonEvolutionDetails = getEvolutionDetailListForEvolutionChain(evolutionChain.chain)
            pokemonEvolutionDetails.forEach {
                insertPokemonEvolutionDetail(it)
                insertPokemonEvolutionDetailJoin(EvolutionDetailJoin(pokemonEvolutionChain.id, it.id))
            }
            insertPokemonEvolutionChain(pokemonEvolutionChain)
        }
    }



}