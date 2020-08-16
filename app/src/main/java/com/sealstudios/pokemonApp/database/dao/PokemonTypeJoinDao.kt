package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin

@Dao
interface PokemonTypeJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin)

    @Update
    suspend fun updatePokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin)

    @Delete
    suspend fun deletePokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin)

    @Query("DELETE FROM PokemonTypesJoin")
    suspend fun deleteAll()

}

