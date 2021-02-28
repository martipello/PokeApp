package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithMovesAndMetaData

@Dao
interface PokemonMoveDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMove(pokemonMove: PokemonMove)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMoves(pokemonMove: List<PokemonMove>)

    @Query("SELECT * FROM PokemonMove ORDER BY move_id ASC")
    fun getAllMoves(): LiveData<List<PokemonMove>>

    @Query("SELECT * FROM PokemonMove WHERE move_name == :name")
    fun getMoveByName(name: String?): LiveData<PokemonMove>

    @Query("SELECT * FROM PokemonMove WHERE move_id == :id")
    fun getMoveById(id: Int?): LiveData<PokemonMove>

    @Query("SELECT * FROM PokemonMove WHERE move_id IN (:ids)")
    fun getMovesByIds(ids: List<Int>): LiveData<List<PokemonMove>>

    @Query("SELECT EXISTS (SELECT * FROM PokemonMove WHERE move_id =:id)")
    suspend fun moveExists(id: Int): Boolean

    @Transaction
    @Query("""SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1""")
    fun getPokemonWithMovesAndMetaDataById(id: Int): LiveData<PokemonWithMovesAndMetaData>

    @Transaction
    @Query("""SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1""")
    suspend fun getPokemonWithMovesAndMetaDataByIdAsync(id: Int): PokemonWithMovesAndMetaData

    @Update
    suspend fun updatePokemonMove(pokemonMove: PokemonMove)

    @Delete
    suspend fun deletePokemonMove(pokemonMove: PokemonMove)

    @Query("DELETE FROM PokemonMove")
    suspend fun deleteAll()

}

