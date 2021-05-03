package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithSpecies
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesForList


@Dao
interface PokemonGraphQLDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonGraphQL(pokemon: PokemonGraphQL)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonGraphQL(pokemon: List<PokemonGraphQL>)

    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemon(): LiveData<List<PokemonGraphQL>>

    @Update
    suspend fun updatePokemon(pokemon: PokemonGraphQL)

    @Delete
    suspend fun deletePokemon(pokemon: PokemonGraphQL)

    @Query("DELETE FROM PokemonGraphQL")
    suspend fun deleteAll()

    @Query(
        """SELECT * FROM PokemonGraphQL WHERE pokemon_name LIKE :search 
                 AND pokemon_types IN (:filters) GROUP BY pokemon_id, pokemon_name 
                 ORDER BY count(*) DESC, pokemon_id ASC"""
    )
    fun searchAndFilterPokemonGraphQL(search: String, filters: List<String>): LiveData<List<PokemonGraphQL>?>

}
