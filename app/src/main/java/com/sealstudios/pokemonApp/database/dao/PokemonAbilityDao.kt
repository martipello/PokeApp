package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility

@Dao
interface PokemonAbilityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbility(pokemonAbility: PokemonAbility)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbilities(pokemonAbilities: List<PokemonAbility>)

    @Query("SELECT * FROM PokemonAbility ORDER BY ability_id ASC")
    fun getAllPokemonAbilities(): LiveData<List<PokemonAbility>>

    @Query("SELECT * FROM PokemonAbility WHERE ability_name == :name")
    fun getPokemonAbilityByName(name: String?): LiveData<PokemonAbility>

    @Query("SELECT * FROM PokemonAbility WHERE ability_id == :id LIMIT 1")
    fun getPokemonAbilityById(id: Int): LiveData<PokemonAbility>

    @Query("SELECT * FROM PokemonAbility WHERE ability_id == :id LIMIT 1")
    suspend fun getPokemonAbilityByIdAsync(id: Int): PokemonAbility

    @Query("SELECT * FROM PokemonAbility WHERE ability_id == :id LIMIT 1")
    suspend fun getPokemonAbilitiesByIdsAsync(id: List<Int>): List<PokemonAbility>

    @Update
    suspend fun updatePokemonAbility(pokemonAbilities: PokemonAbility)

    @Delete
    suspend fun deletePokemonAbility(pokemonAbilities: PokemonAbility)

    @Query("DELETE FROM PokemonAbility")
    suspend fun deleteAll()

}

