package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.ChainLink
import com.sealstudios.pokemonApp.api.`object`.EvolutionChain
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonEvolutionDetailJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonEvolutionChainWithDetailList
import com.sealstudios.pokemonApp.database.dao.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonEvolutionRepository @Inject constructor(
        private val pokemonEvolutionChainDao: PokemonEvolutionChainDao,
        private val pokemonEvolutionDetailDao: PokemonEvolutionDetailDao,
        private val pokemonEvolutionDetailJoinDao: PokemonEvolutionDetailJoinDao,
) {

    suspend fun getPokemonEvolutionChainWithDetailListByIdAsync(id: Int): PokemonEvolutionChainWithDetailList? {
        return withContext(Dispatchers.IO) {
            return@withContext pokemonEvolutionChainDao.getPokemonEvolutionChainWithDetailListById(id)
        }
    }

    private suspend fun insertPokemonEvolutionChain(pokemonEvolutionChain: PokemonEvolutionChain) {
        pokemonEvolutionChainDao.insertPokemonEvolutionChain(pokemonEvolutionChain)
    }

    private suspend fun insertPokemonEvolutionDetail(pokemonEvolutionDetail: PokemonEvolutionDetail) {
        pokemonEvolutionDetailDao.insertPokemonEvolutionDetail(pokemonEvolutionDetail)
    }

    private suspend fun insertPokemonEvolutionDetailJoin(pokemonEvolutionDetailJoin: PokemonEvolutionDetailJoin) {
        pokemonEvolutionDetailJoinDao.insertPokemonEvolutionDetailJoin(pokemonEvolutionDetailJoin)
    }

    suspend fun insertPokemonEvolution(
            evolutionChain: EvolutionChain
    ) {
        withContext(Dispatchers.IO) {
            val pokemonEvolutionChain = PokemonEvolutionChain.mapToEvolutionChain(evolutionChain)
            evolutionChain.chain.evolves_to.map { chainLink ->
                if (chainLink.evolution_details.isNotEmpty()) {
                    val pokemonEvolutionDetailList = pokemonEvolutionDetailList(chainLink, mutableListOf())
                    pokemonEvolutionDetailList.map {
                        insertPokemonEvolutionDetail(it)
                        insertPokemonEvolutionDetailJoin(PokemonEvolutionDetailJoin(pokemonEvolutionChain.id, it.id))
                    }
                }
            }
            insertPokemonEvolutionChain(pokemonEvolutionChain)
        }
    }


    private fun pokemonEvolutionDetailList(chainLink: ChainLink, idList: MutableList<PokemonEvolutionDetail>): List<PokemonEvolutionDetail> {
        idList.add(PokemonEvolutionDetail.mapToPokemonEvolutionDetails(chainLink.evolution_details.first(), chainLink))
        while (chainLink.evolves_to.isNotEmpty()) {
            return pokemonEvolutionDetailList(chainLink.evolves_to.first(), idList)
        }
        return idList
    }


}