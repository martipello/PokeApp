package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Species

@Dao
interface SpeciesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpecies(species: Species)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpecies(species: List<Species>)

    @Query("SELECT * FROM Species ORDER BY species_id ASC")
    fun getAllSpecies(): LiveData<List<Species>>

    @Query("SELECT * FROM Species WHERE species_name == :name")
    fun getSingleSpeciesByName(name: String?): LiveData<Species>

    @Query("SELECT * FROM Species WHERE species_id == :id LIMIT 1")
    suspend fun getSinglePokemonWithSpeciesByIdAsync(id: Int): Species?

    @Update
    suspend fun updateSpecies(species: Species)

    @Delete
    suspend fun deleteSpecies(species: Species)

    @Query("DELETE FROM Pokemon")
    suspend fun deleteAll()

}

