package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaData

@Dao
interface PokemonMoveMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaData(pokemonPokemonMove: PokemonMoveMetaData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoveMetaDatas(pokemonPokemonMove: List<PokemonMoveMetaData>)

    @Query("SELECT * FROM PokemonMoveMetaData WHERE meta_move_id IN (:pokemonAndMoveIds)")
    suspend fun getMoveMetaDataByIdsAsync(pokemonAndMoveIds: List<Int>): List<PokemonMoveMetaData>

    @Update
    suspend fun updateMoveMetaData(pokemonMoveMetaData: PokemonMoveMetaData)

    @Delete
    suspend fun deleteMoveMetaData(pokemonMoveMetaData: PokemonMoveMetaData)

    @Query("DELETE FROM PokemonMoveMetaData")
    suspend fun deleteAll()

}

