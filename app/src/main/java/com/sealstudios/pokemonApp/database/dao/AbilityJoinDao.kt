package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityJoin

@Dao
interface AbilityJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbilityJoin(abilityJoin: AbilityJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbilitiesJoin(abilityJoin: List<AbilityJoin>)

    @Update
    suspend fun updatePokemonAbilityJoin(abilityJoin: AbilityJoin)

    @Delete
    suspend fun deletePokemonAbilityJoin(abilityJoin: AbilityJoin)

    @Query("DELETE FROM AbilityJoin")
    suspend fun deleteAll()

}

