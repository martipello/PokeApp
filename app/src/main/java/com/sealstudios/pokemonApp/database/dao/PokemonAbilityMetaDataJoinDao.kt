package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaDataJoin

@Dao
interface PokemonAbilityMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaDataJoins(pokemonAbilityMetaDataJoin: List<PokemonAbilityMetaDataJoin>)

    @Update
    suspend fun updateAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin)

    @Delete
    suspend fun deleteAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin)

    @Query("DELETE FROM PokemonAbilityMetaDataJoin")
    suspend fun deleteAll()

}

