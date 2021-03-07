package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonTypeMetaData

@Dao
interface PokemonTypeMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeMetaData(pokemonTypeMetaData: PokemonTypeMetaData)

    @Update
    suspend fun updatePokemonTypeMetaData(pokemonTypeMetaData: PokemonTypeMetaData)

    @Delete
    suspend fun deletePokemonType(pokemonType: PokemonTypeMetaData)

    @Query("DELETE FROM PokemonTypeMetaData")
    suspend fun deleteAll()

}

