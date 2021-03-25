package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.TypesJoin

@Dao
interface TypeJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeJoin(typeJoin: TypesJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeJoins(typeJoin: List<TypesJoin>)

    @Update
    suspend fun updatePokemonTypeJoin(typeJoin: TypesJoin)

    @Delete
    suspend fun deletePokemonTypeJoin(typeJoin: TypesJoin)

    @Query("DELETE FROM TypesJoin")
    suspend fun deleteAll()

}

