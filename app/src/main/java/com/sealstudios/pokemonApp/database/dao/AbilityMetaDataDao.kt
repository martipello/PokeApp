package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.AbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithAbilitiesAndMetaData

@Dao
interface AbilityMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaData(abilityMetaData: AbilityMetaData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaDataList(abilityMetaDataList: List<AbilityMetaData>)

    @Query("SELECT * FROM AbilityMetaData WHERE ability_meta_data_id IN (:pokemonAbilityMetaDataIds)")
    suspend fun getAbilityMetaDataByIdsAsync(pokemonAbilityMetaDataIds: List<Int>): List<AbilityMetaData>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1")
    fun getPokemonWithAbilitiesAndMetaDataById(id: Int): LiveData<PokemonWithAbilitiesAndMetaData>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1")
    suspend fun getPokemonWithAbilitiesAndMetaDataByIdAsync(id: Int): PokemonWithAbilitiesAndMetaData

    @Update
    suspend fun updateAbilityMetaData(abilityMetaData: AbilityMetaData)

    @Delete
    suspend fun deleteAbilityMetaData(abilityMetaData: AbilityMetaData)

    @Query("DELETE FROM AbilityMetaData")
    suspend fun deleteAll()

}

