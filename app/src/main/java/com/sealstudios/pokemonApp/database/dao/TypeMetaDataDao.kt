package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.TypeMetaData

@Dao
interface TypeMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeMetaData(typeMetaData: TypeMetaData)

    @Update
    suspend fun updatePokemonTypeMetaData(typeMetaData: TypeMetaData)

    @Delete
    suspend fun deletePokemonType(type: TypeMetaData)

    @Query("DELETE FROM TypeMetaData")
    suspend fun deleteAll()

}

