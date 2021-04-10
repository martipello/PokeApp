package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Type
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes

@Dao
interface TypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonType(type: Type)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypes(type: List<Type>)

    @Update
    suspend fun updatePokemonType(type: Type)

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_id == :id LIMIT 1")
    suspend fun getPokemonWithTypesById(id: Int): PokemonWithTypes

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun getPokemonWithTypes(search: String?): LiveData<List<PokemonWithTypes>>

    @Delete
    suspend fun deletePokemonType(type: Type)

    @Query("DELETE FROM Type")
    suspend fun deleteAll()

}

