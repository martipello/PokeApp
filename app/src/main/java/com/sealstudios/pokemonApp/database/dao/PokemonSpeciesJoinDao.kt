package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonSpeciesJoin

@Dao
interface PokemonSpeciesJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin)

    @Update
    suspend fun updatePokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin)

    @Delete
    suspend fun deletePokemonSpeciesJoin(pokemonSpeciesJoin: PokemonSpeciesJoin)

    @Query("DELETE FROM PokemonSpeciesJoin")
    suspend fun deleteAll()

}

