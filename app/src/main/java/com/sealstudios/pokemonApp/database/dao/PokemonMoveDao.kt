package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes

@Dao
interface PokemonMoveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonMove(pokemonMove: PokemonMove)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonMoves(pokemonMove: List<PokemonMove>)

    @Query("SELECT * FROM PokemonMove ORDER BY move_id ASC")
    fun getAllMoves(): LiveData<List<PokemonMove>>

    @Query("SELECT * FROM PokemonMove WHERE move_name == :name")
    fun getSinglePokemonByName(name: String?): LiveData<PokemonMove>

    @Query("SELECT * FROM PokemonMove WHERE move_id == :id")
    fun getSinglePokemonById(id: Int?): LiveData<PokemonMove>

    @Update
    suspend fun updatePokemonMove(pokemonMove: PokemonMove)

    @Delete
    suspend fun deletePokemonMove(pokemonMove: PokemonMove)

    @Query("DELETE FROM PokemonMove")
    suspend fun deleteAll()

//    @Transaction
//    @Query("SELECT * FROM pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
//    fun getPokemonWithTypes(search: String?): LiveData<List<PokemonWithTypes>>

}

