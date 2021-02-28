package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonAbilityJoin

@Dao
interface PokemonAbilityJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbilityJoin(pokemonAbilityJoin: PokemonAbilityJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbilitiesJoin(pokemonAbilityJoin: List<PokemonAbilityJoin>)

    @Update
    suspend fun updatePokemonAbilityJoin(pokemonAbilityJoin: PokemonAbilityJoin)

    @Delete
    suspend fun deletePokemonAbilityJoin(pokemonAbilityJoin: PokemonAbilityJoin)

    @Query("DELETE FROM PokemonAbilityJoin")
    suspend fun deleteAll()

}

