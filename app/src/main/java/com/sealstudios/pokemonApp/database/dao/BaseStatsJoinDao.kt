package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.BaseStatsJoin

@Dao
interface BaseStatsJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStatsJoin(baseStatsJoin: BaseStatsJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStatsJoinsList(baseStatsJoin: List<BaseStatsJoin>)

    @Update
    suspend fun updatePokemonBaseStatsJoin(baseStatsJoin: BaseStatsJoin)

    @Delete
    suspend fun deletePokemonBaseStatsJoin(baseStatsJoin: BaseStatsJoin)

    @Query("DELETE FROM BaseStatsJoin")
    suspend fun deleteAll()

}

