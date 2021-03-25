package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityMetaDataJoin

@Dao
interface AbilityMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaDataJoin(abilityMetaDataJoin: AbilityMetaDataJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaDataJoins(abilityMetaDataJoin: List<AbilityMetaDataJoin>)

    @Update
    suspend fun updateAbilityMetaDataJoin(abilityMetaDataJoin: AbilityMetaDataJoin)

    @Delete
    suspend fun deleteAbilityMetaDataJoin(abilityMetaDataJoin: AbilityMetaDataJoin)

    @Query("DELETE FROM AbilityMetaDataJoin")
    suspend fun deleteAll()

}

