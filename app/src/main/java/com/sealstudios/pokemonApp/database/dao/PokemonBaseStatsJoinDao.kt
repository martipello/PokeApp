package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStatsJoin

@Dao
interface PokemonBaseStatsJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStatsJoinsList(pokemonBaseStatsJoin: List<PokemonBaseStatsJoin>)

    @Update
    suspend fun updatePokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin)

    @Delete
    suspend fun deletePokemonBaseStatsJoin(pokemonBaseStatsJoin: PokemonBaseStatsJoin)

    @Query("DELETE FROM PokemonBaseStatsJoin")
    suspend fun deleteAll()

}

