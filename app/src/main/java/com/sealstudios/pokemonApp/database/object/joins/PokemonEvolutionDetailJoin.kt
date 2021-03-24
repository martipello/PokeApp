package com.sealstudios.pokemonApp.database.`object`.joins

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionChain.Companion.POKEMON_EVOLUTION_CHAIN_ID
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionDetail.Companion.POKEMON_EVOLUTION_DETAILS_ID
import org.jetbrains.annotations.NotNull

@Entity(primaryKeys = [POKEMON_EVOLUTION_CHAIN_ID, POKEMON_EVOLUTION_DETAILS_ID])
class PokemonEvolutionDetailJoin(
        @NotNull
        @ColumnInfo(name = POKEMON_EVOLUTION_CHAIN_ID, index = true)
        val pokemon_evolution_chain_id: Int,

        @NotNull
        @ColumnInfo(name = POKEMON_EVOLUTION_DETAILS_ID, index = true)
        val pokemon_evolution_details_id: Int

)