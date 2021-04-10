package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.TypeMetaDataJoin

@Dao
interface TypeMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeMetaData(typeMetaDataJoin: TypeMetaDataJoin)

    @Update
    suspend fun updatePokemonTypeMetaData(typeMetaDataJoin: TypeMetaDataJoin)

    @Delete
    suspend fun deletePokemonType(typeMetaDataJoin: TypeMetaDataJoin)

    @Query("DELETE FROM TypeMetaDataJoin")
    suspend fun deleteAll()

}

