package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonType

@Dao
interface PokemonTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonType(pokemonType: PokemonType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonTypes(pokemonType: List<PokemonType>)

    @Update
    suspend fun updatePokemonType(pokemonType: PokemonType)

    @Delete
    suspend fun deletePokemonType(pokemonType: PokemonType)

    @Query("DELETE FROM PokemonType")
    suspend fun deleteAll()

//    @Transaction
//    @Query("SELECT * FROM PokemonType WHERE type_name IN (:typeFilters) ORDER BY type_id ASC")
//    fun getTypesWithPokemonForTypes(typeFilters: List<String>): LiveData<List<TypesWithPokemon>>
//
//    @Transaction
//    @Query("SELECT * FROM PokemonType WHERE type_name IN (:typeFilters) ORDER BY type_id ASC")
//    fun getTypesWithPokemonForTypesAndSearch(typeFilters: List<String>): LiveData<List<TypesWithPokemon>>


    //@Query("SELECT ATTRIBUTES.* FROM ATTRIBUTES INNER JOIN PRODUCTS_ATTRIBUTES ON PRODUCTS_ATTRIBUTES._ATTRIBUTE_ID = ATTRIBUTES._ID
    // INNER JOIN PRODUCTS ON PRODUCTS._ID = PRODUCTS_ATTRIBUTES._PRODUCT_ID WHERE PRODUCTS._ID = :productId
    // ORDER BY PRODUCTS_ATTRIBUTES.DISPLAY_ORDERING ASC")

}

