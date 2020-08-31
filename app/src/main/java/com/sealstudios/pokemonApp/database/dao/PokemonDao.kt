package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: Pokemon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: List<Pokemon>)

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
    suspend fun updatePokemon(pokemon: Pokemon)

    @Delete
    suspend fun deletePokemon(pokemon: Pokemon)

    @Query("DELETE FROM Pokemon")
    suspend fun deleteAll()

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypes                                      ///
    /// ************************************************************************************* ///

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_id == :id LIMIT 1")
    fun getSinglePokemonWithTypesById(id: Int?): LiveData<PokemonWithTypes>

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun getPokemonWithTypes(search: String?): LiveData<List<PokemonWithTypes>>

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpecies                            ///
    /// ************************************************************************************* ///

    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithTypesAndSpecies(): LiveData<List<PokemonWithTypesAndSpecies>>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonWithTypesAndSpeciesById(id: Int?): LiveData<PokemonWithTypesAndSpecies>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAllPokemonWithTypesAndSpecies(search: String): LiveData<List<PokemonWithTypesAndSpecies>>

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpeciesAndMoves                    ///
    /// ************************************************************************************* ///

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonWithTypesAndSpeciesAndMovesById(id: Int?): LiveData<PokemonWithTypesAndSpeciesAndMoves>

}

