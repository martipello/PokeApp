package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.MoveMetaData

@Dao
interface MoveMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaData(pokemonMove: MoveMetaData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaDatas(pokemonMove: List<MoveMetaData>)

    @Query("SELECT * FROM MoveMetaData WHERE meta_move_id IN (:pokemonAndMoveIds)")
    suspend fun getMoveMetaDataByIdsAsync(pokemonAndMoveIds: List<Int>): List<MoveMetaData>

    @Update
    suspend fun updateMoveMetaData(moveMetaData: MoveMetaData)

    @Delete
    suspend fun deleteMoveMetaData(moveMetaData: MoveMetaData)

    @Query("DELETE FROM MoveMetaData")
    suspend fun deleteAll()

}

