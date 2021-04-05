package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail

@Dao
interface EvolutionDetailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetail(evolutionDetail: EvolutionDetail)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetailList(evolutionDetailList: List<EvolutionDetail>)

    @Update
    suspend fun updatePokemonEvolutionDetail(evolutionDetail: EvolutionDetail)

    @Transaction
    @Query("SELECT * FROM EvolutionDetail WHERE pokemon_evolution_details_id == :id LIMIT 1")
    suspend fun getPokemonEvolutionDetailById(id: Int): EvolutionDetail?

    @Delete
    suspend fun deletePokemonEvolutionDetail(evolutionDetail: EvolutionDetail)

    @Query("DELETE FROM EvolutionDetail")
    suspend fun deleteAll()

}

