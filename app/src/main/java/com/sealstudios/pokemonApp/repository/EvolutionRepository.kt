package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.database.`object`.EvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail
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
            val pokemonEvolutionChain = EvolutionChain.mapToEvolutionChain(evolutionChain)
            evolutionChain.chain.evolves_to.map { chainLink ->
                if (chainLink.evolution_details.isNotEmpty()) {
                    val pokemonEvolutionDetailList = pokemonEvolutionDetailList(chainLink, mutableListOf())
                    pokemonEvolutionDetailList.map {
                        insertPokemonEvolutionDetail(it)
                        insertPokemonEvolutionDetailJoin(EvolutionDetailJoin(pokemonEvolutionChain.id, it.id))
                    }
                }
            }
            insertPokemonEvolutionChain(pokemonEvolutionChain)
        }
    }


    private fun pokemonEvolutionDetailList(chainLink: ChainLink, idList: MutableList<EvolutionDetail>): List<EvolutionDetail> {
        idList.add(EvolutionDetail.mapToPokemonEvolutionDetails(chainLink.evolution_details.first(), chainLink))
        while (chainLink.evolves_to.isNotEmpty()) {
            return pokemonEvolutionDetailList(chainLink.evolves_to.first(), idList)
        }
        return idList
    }


}