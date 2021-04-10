package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Move
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithMovesAndMetaData

@Dao
interface MoveDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMove(move: Move)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMoves(move: List<Move>)

    @Query("SELECT * FROM Move ORDER BY move_id ASC")
    fun getAllMoves(): LiveData<List<Move>>

    @Query("SELECT * FROM Move WHERE move_name == :name")
    fun getMoveByName(name: String?): LiveData<Move>

    @Query("SELECT * FROM Move WHERE move_id == :id")
    fun getMoveById(id: Int?): LiveData<Move>

    @Query("SELECT * FROM Move WHERE move_id IN (:ids)")
    fun getMovesByIds(ids: List<Int>): LiveData<List<Move>>

    @Query("SELECT EXISTS (SELECT * FROM Move WHERE move_id =:id)")
    suspend fun moveExists(id: Int): Boolean

    @Transaction
    @Query("""SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1""")
    fun getPokemonWithMovesAndMetaDataById(id: Int): LiveData<PokemonWithMovesAndMetaData>

    @Transaction
    @Query("""SELECT * FROM Pokemon WHERE Pokemon.pokemon_id == :id LIMIT 1""")
    suspend fun getPokemonWithMovesAndMetaDataByIdAsync(id: Int): PokemonWithMovesAndMetaData

    @Update
    suspend fun updatePokemonMove(move: Move)

    @Delete
    suspend fun deletePokemonMove(move: Move)

    @Query("DELETE FROM Move")
    suspend fun deleteAll()

}

