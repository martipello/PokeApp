package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionDetail

@Dao
interface PokemonEvolutionDetailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetail(pokemonEvolutionDetail: PokemonEvolutionDetail)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetailList(pokemonEvolutionDetailList: List<PokemonEvolutionDetail>)

    @Update
    suspend fun updatePokemonEvolutionDetail(pokemonEvolutionDetail: PokemonEvolutionDetail)

    @Transaction
    @Query("SELECT * FROM PokemonEvolutionDetail WHERE pokemon_evolution_details_id == :id LIMIT 1")
    suspend fun getPokemonEvolutionDetailById(id: Int): PokemonEvolutionDetail

    @Delete
    suspend fun deletePokemonEvolutionDetail(pokemonEvolutionDetail: PokemonEvolutionDetail)

    @Query("DELETE FROM PokemonEvolutionDetail")
    suspend fun deleteAll()

}

