package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.EvolutionChain
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.joins.EvolutionDetailJoin

data class EvolutionChainWithDetailList(
        @Embedded
        val evolutionChain: EvolutionChain,
        @Relation(
                parentColumn = EvolutionChain.POKEMON_EVOLUTION_CHAIN_ID,
                entity = EvolutionDetail::class,
                entityColumn = EvolutionDetail.POKEMON_EVOLUTION_DETAILS_ID,
                associateBy = Junction(
                        value = EvolutionDetailJoin::class,
                        parentColumn = EvolutionChain.POKEMON_EVOLUTION_CHAIN_ID,
                        entityColumn = EvolutionDetail.POKEMON_EVOLUTION_DETAILS_ID
                )
        )
        val evolutionChainLinkList: List<EvolutionDetail>
)