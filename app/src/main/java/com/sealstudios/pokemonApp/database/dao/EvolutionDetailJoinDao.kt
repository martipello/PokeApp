package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.EvolutionDetailJoin

@Dao
interface EvolutionDetailJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetailJoin(evolutionDetailJoin: EvolutionDetailJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetailListJoin(evolutionDetailJoin: List<EvolutionDetailJoin>)

    @Update
    suspend fun updatePokemonEvolutionDetailJoin(evolutionDetailJoin: EvolutionDetailJoin)

    @Delete
    suspend fun deletePokemonEvolutionDetailJoin(evolutionDetailJoin: EvolutionDetailJoin)

    @Query("DELETE FROM EvolutionDetailJoin")
    suspend fun deleteAll()

}

