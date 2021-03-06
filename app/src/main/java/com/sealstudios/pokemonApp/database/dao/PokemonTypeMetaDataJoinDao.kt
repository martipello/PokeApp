package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypeMetaDataJoin

@Dao
interface PokemonTypeMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeMetaData(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin)

    @Update
    suspend fun updatePokemonTypeMetaData(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin)

    @Delete
    suspend fun deletePokemonType(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin)

    @Query("DELETE FROM PokemonTypeMetaDataJoin")
    suspend fun deleteAll()

}

