package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.EvolutionChain
import com.sealstudios.pokemonApp.database.`object`.relations.EvolutionChainWithDetailList

@Dao
interface EvolutionChainDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionChain(evolutionChain: EvolutionChain)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionChainList(evolutionChain: List<EvolutionChain>)

    @Update
    suspend fun updatePokemonEvolutionChain(evolutionChain: EvolutionChain)

    @Transaction
    @Query("SELECT * FROM EvolutionChain WHERE pokemon_evolution_chain_id == :id LIMIT 1")
    suspend fun getPokemonEvolutionChainById(id: Int): EvolutionChain

    @Transaction
    @Query("SELECT * FROM EvolutionChain WHERE pokemon_evolution_chain_id == :id LIMIT 1")
    suspend fun getPokemonEvolutionChainWithDetailListById(id: Int): EvolutionChainWithDetailList?

    @Delete
    suspend fun deletePokemonEvolutionChain(evolutionChain: EvolutionChain)

    @Query("DELETE FROM EvolutionChain")
    suspend fun deleteAll()

}

