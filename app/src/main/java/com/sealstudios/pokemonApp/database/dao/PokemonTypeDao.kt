package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonType

@Dao
interface PokemonTypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonType(pokemonType: PokemonType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypes(pokemonType: List<PokemonType>)

    @Update
    suspend fun updatePokemonType(pokemonType: PokemonType)

    @Delete
    suspend fun deletePokemonType(pokemonType: PokemonType)

    @Query("DELETE FROM PokemonType")
    suspend fun deleteAll()

}

