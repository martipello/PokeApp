package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.MovesJoin

@Dao
interface MoveJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMoveJoin(moveJoin: MovesJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMoveJoin(moveJoin: List<MovesJoin>)

    @Update
    suspend fun updatePokemonMoveJoin(moveJoin: MovesJoin)

    @Delete
    suspend fun deletePokemonMoveJoin(moveJoin: MovesJoin)

    @Query("DELETE FROM MovesJoin")
    suspend fun deleteAll()

}

