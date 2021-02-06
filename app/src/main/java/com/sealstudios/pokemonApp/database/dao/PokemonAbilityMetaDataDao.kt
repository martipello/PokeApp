package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonWithAbilitiesAndMetaData

@Dao
interface PokemonAbilityMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaData(pokemonAbilityMetaData: PokemonAbilityMetaData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityMetaDataList(pokemonAbilityMetaDataList: List<PokemonAbilityMetaData>)

    @Query("SELECT * FROM PokemonAbilityMetaData WHERE ability_meta_data_id IN (:pokemonAbilityMetaDataIds)")
    suspend fun getAbilityMetaDataByIdsAsync(pokemonAbilityMetaDataIds: List<Int>): List<PokemonAbilityMetaData>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1")
    fun getPokemonWithAbilitiesAndMetaDataById(id: Int): LiveData<PokemonWithAbilitiesAndMetaData>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1")
    suspend fun getPokemonWithAbilitiesAndMetaDataByIdAsync(id: Int): PokemonWithAbilitiesAndMetaData

    @Update
    suspend fun updateAbilityMetaData(pokemonAbilityMetaData: PokemonAbilityMetaData)

    @Delete
    suspend fun deleteAbilityMetaData(pokemonAbilityMetaData: PokemonAbilityMetaData)

    @Query("DELETE FROM PokemonAbilityMetaData")
    suspend fun deleteAll()

}

