package com.sealstudios.pokemonApp.database.`object`.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonEvolutionDetailJoin

data class PokemonEvolutionChainWithDetailList(
        @Embedded
        val pokemonEvolutionChain: PokemonEvolutionChain,
        @Relation(
                parentColumn = PokemonEvolutionChain.POKEMON_EVOLUTION_CHAIN_ID,
                entity = PokemonEvolutionDetail::class,
                entityColumn = PokemonEvolutionDetail.POKEMON_EVOLUTION_DETAILS_ID,
                associateBy = Junction(
                        value = PokemonEvolutionDetailJoin::class,
                        parentColumn = PokemonEvolutionChain.POKEMON_EVOLUTION_CHAIN_ID,
                        entityColumn = PokemonEvolutionDetail.POKEMON_EVOLUTION_DETAILS_ID
                )
        )
        val pokemonEvolutionChainLinkList: List<PokemonEvolutionDetail>
)