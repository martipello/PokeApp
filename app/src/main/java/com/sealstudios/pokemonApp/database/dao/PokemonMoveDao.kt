package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes

@Dao
interface PokemonMoveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonMove(pokemonMove: Pokemon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonMoves(pokemonMove: List<Pokemon>)

    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemon(): LiveData<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAllPokemon(search: String?): LiveData<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchPokemonWithTypeFilters(search: String?): LiveData<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name == :name")
    fun getSinglePokemonByName(name: String?): LiveData<Pokemon>

    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonById(id: Int?): LiveData<Pokemon>

    @Update
    suspend fun updatePokemonMove(pokemonMove: Pokemon)

    @Delete
    suspend fun deletePokemonMove(pokemonMove: Pokemon)

    @Query("DELETE FROM Pokemon")
    suspend fun deleteAll()

//    @Transaction
//    @Query("SELECT * FROM pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
//    fun getPokemonWithTypes(search: String?): LiveData<List<PokemonWithTypes>>

}

