package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: Pokemon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: List<Pokemon>)

    @Query("SELECT * FROM pokemon_table ORDER BY id ASC")
    fun getAllPokemon(): LiveData<List<Pokemon>>

    @Query("SELECT * FROM pokemon_table WHERE name LIKE :search ORDER BY id ASC")
    fun searchAllPokemon(search: String?): LiveData<List<Pokemon>>

    @Query("SELECT * FROM pokemon_table WHERE name LIKE :search ORDER BY id ASC")
    fun searchPokemonWithTypeFilters(search: String?): LiveData<List<Pokemon>>

    @Query("SELECT * FROM pokemon_table WHERE name == :name")
    fun getSinglePokemonByName(name: String?): LiveData<Pokemon>

    @Query("SELECT * FROM pokemon_table WHERE id == :id")
    fun getSinglePokemonById(id: Int?): LiveData<Pokemon>

    @Update
    suspend fun updatePokemon(pokemon: Pokemon)

    @Delete
    suspend fun deletePokemon(pokemon: Pokemon)

    @Query("DELETE FROM pokemon_table")
    suspend fun deleteAll()
}

