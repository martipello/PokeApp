package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionChain
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonEvolutionChainWithDetailList

@Dao
interface PokemonEvolutionChainDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionChain(pokemonEvolutionChain: PokemonEvolutionChain)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionChainList(pokemonEvolutionChain: List<PokemonEvolutionChain>)

    @Update
    suspend fun updatePokemonEvolutionChain(pokemonEvolutionChain: PokemonEvolutionChain)

    @Transaction
    @Query("SELECT * FROM PokemonEvolutionChain WHERE pokemon_evolution_chain_id == :id LIMIT 1")
    suspend fun getPokemonEvolutionChainById(id: Int): PokemonEvolutionChain

    @Transaction
    @Query("SELECT * FROM PokemonEvolutionChain WHERE pokemon_evolution_chain_id == :id LIMIT 1")
    suspend fun getPokemonEvolutionChainWithDetailListById(id: Int): PokemonEvolutionChainWithDetailList?

    @Delete
    suspend fun deletePokemonEvolutionChain(pokemonEvolutionChain: PokemonEvolutionChain)

    @Query("DELETE FROM PokemonEvolutionChain")
    suspend fun deleteAll()

}

