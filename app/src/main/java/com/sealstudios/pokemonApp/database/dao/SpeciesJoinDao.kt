package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.SpeciesJoin

@Dao
interface SpeciesJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonSpeciesJoin(speciesJoin: SpeciesJoin)

    @Update
    suspend fun updatePokemonSpeciesJoin(speciesJoin: SpeciesJoin)

    @Delete
    suspend fun deletePokemonSpeciesJoin(speciesJoin: SpeciesJoin)

    @Query("DELETE FROM SpeciesJoin")
    suspend fun deleteAll()

}

