package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.BaseStats
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithBaseStats

@Dao
interface BaseStatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStats(baseStats: BaseStats)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonBaseStatsList(baseStatsList: List<BaseStats>)

    @Update
    suspend fun updatePokemonBaseStats(baseStats: BaseStats)

    @Transaction
    @Query("SELECT * FROM BaseStats WHERE pokemon_stat_id == :id LIMIT 1")
    fun getPokemonStatsById(id: Int?): LiveData<BaseStats>

    @Transaction
    @Query("SELECT * FROM BaseStats WHERE pokemon_stat_id == :id LIMIT 1")
    suspend fun getPokemonBaseStatsByIdAsync(id: Int?): BaseStats

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id LIMIT 1")
    suspend fun getPokemonWithStatsByIdAsync(id: Int?): PokemonWithBaseStats

    @Delete
    suspend fun deletePokemonBaseStats(baseStats: BaseStats)

    @Query("DELETE FROM BaseStats")
    suspend fun deleteAll()

}

