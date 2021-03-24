package com.sealstudios.pokemonApp.database.dao

import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonEvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonEvolutionDetailJoin

@Dao
interface PokemonEvolutionDetailJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetailJoin(pokemonEvolutionDetailJoin: PokemonEvolutionDetailJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonEvolutionDetailListJoin(pokemonEvolutionDetailJoin: List<PokemonEvolutionDetailJoin>)

    @Update
    suspend fun updatePokemonEvolutionDetailJoin(pokemonEvolutionDetailJoin: PokemonEvolutionDetailJoin)

    @Delete
    suspend fun deletePokemonEvolutionDetailJoin(pokemonEvolutionDetailJoin: PokemonEvolutionDetailJoin)

    @Query("DELETE FROM PokemonEvolutionDetailJoin")
    suspend fun deleteAll()

}

