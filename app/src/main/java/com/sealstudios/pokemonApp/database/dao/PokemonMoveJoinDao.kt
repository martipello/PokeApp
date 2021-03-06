package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonMovesJoin

@Dao
interface PokemonMoveJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMoveJoin(pokemonMoveJoin: PokemonMovesJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonMoveJoin(pokemonMoveJoin: List<PokemonMovesJoin>)

    @Update
    suspend fun updatePokemonMoveJoin(pokemonMoveJoin: PokemonMovesJoin)

    @Delete
    suspend fun deletePokemonMoveJoin(pokemonMoveJoin: PokemonMovesJoin)

    @Query("DELETE FROM PokemonMovesJoin")
    suspend fun deleteAll()

}

