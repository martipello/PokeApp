package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaDataJoin

@Dao
interface PokemonMoveMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaDataJoins(pokemonMoveMetaDataJoin: List<PokemonMoveMetaDataJoin>)

    @Update
    suspend fun updateMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin)

    @Delete
    suspend fun deleteMoveMetaDataJoin(pokemonMoveMetaDataJoin: PokemonMoveMetaDataJoin)

    @Query("DELETE FROM PokemonMoveMetaDataJoin")
    suspend fun deleteAll()

}

