package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies

@Dao
interface PokemonSpeciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecies(pokemonSpecies: PokemonSpecies)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecies(pokemonSpecies: List<PokemonSpecies>)

    @Query("SELECT * FROM PokemonSpecies ORDER BY species_id ASC")
    fun getAllSpecies(): LiveData<List<PokemonSpecies>>

    @Query("SELECT * FROM PokemonSpecies WHERE species_name == :name")
    fun getSingleSpeciesByName(name: String?): LiveData<PokemonSpecies>

    @Query("SELECT * FROM PokemonSpecies WHERE species_id == :id")
    fun getSingleSpeciesById(id: Int?): LiveData<PokemonSpecies>

    @Update
    suspend fun updateSpecies(pokemonSpecies: PokemonSpecies)

    @Delete
    suspend fun deleteSpecies(pokemonSpecies: PokemonSpecies)

    @Query("DELETE FROM Pokemon")
    suspend fun deleteAll()

}
