package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes

@Dao
interface PokemonTypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonType(pokemonType: PokemonType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypes(pokemonType: List<PokemonType>)

    @Update
    suspend fun updatePokemonType(pokemonType: PokemonType)

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_id == :id LIMIT 1")
    fun getSinglePokemonWithTypesById(id: Int?): LiveData<PokemonWithTypes>

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_id == :id LIMIT 1")
    suspend fun getSinglePokemonWithTypesByIdAsync(id: Int?): PokemonWithTypes

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun getPokemonWithTypes(search: String?): LiveData<List<PokemonWithTypes>>

    @Delete
    suspend fun deletePokemonType(pokemonType: PokemonType)

    @Query("DELETE FROM PokemonType")
    suspend fun deleteAll()

}

