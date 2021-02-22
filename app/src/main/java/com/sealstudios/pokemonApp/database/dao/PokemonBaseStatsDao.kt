package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithBaseStats

@Dao
interface PokemonBaseStatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStats(pokemonBaseStats: PokemonBaseStats)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStatsList(pokemonBaseStatsList: List<PokemonBaseStats>)

    @Update
    suspend fun updatePokemonBaseStats(pokemonBaseStats: PokemonBaseStats)

    @Transaction
    @Query("SELECT * FROM PokemonBaseStats WHERE pokemon_stat_id == :id LIMIT 1")
    fun getPokemonStatsById(id: Int?): LiveData<PokemonBaseStats>

    @Transaction
    @Query("SELECT * FROM PokemonBaseStats WHERE pokemon_stat_id == :id LIMIT 1")
    suspend fun getPokemonBaseStatsByIdAsync(id: Int?): PokemonBaseStats

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id LIMIT 1")
    suspend fun getPokemonWithStatsByIdAsync(id: Int?): PokemonWithBaseStats

    @Delete
    suspend fun deletePokemonBaseStats(pokemonBaseStats: PokemonBaseStats)

    @Query("DELETE FROM PokemonBaseStats")
    suspend fun deleteAll()

}

