package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.MoveMetaDataJoin

@Dao
interface MoveMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaDataJoin(moveMetaDataJoin: MoveMetaDataJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaDataJoins(moveMetaDataJoin: List<MoveMetaDataJoin>)

    @Update
    suspend fun updateMoveMetaDataJoin(moveMetaDataJoin: MoveMetaDataJoin)

    @Delete
    suspend fun deleteMoveMetaDataJoin(moveMetaDataJoin: MoveMetaDataJoin)

    @Query("DELETE FROM MoveMetaDataJoin")
    suspend fun deleteAll()

}

