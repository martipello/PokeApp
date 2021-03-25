package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.Ability

@Dao
interface AbilityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbility(ability: Ability)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonAbilities(abilities: List<Ability>)

    @Query("SELECT * FROM Ability ORDER BY ability_id ASC")
    fun getAllPokemonAbilities(): LiveData<List<Ability>>

    @Query("SELECT * FROM Ability WHERE ability_name == :name")
    fun getPokemonAbilityByName(name: String?): LiveData<Ability>

    @Query("SELECT * FROM Ability WHERE ability_id == :id LIMIT 1")
    fun getPokemonAbilityById(id: Int): LiveData<Ability>

    @Query("SELECT * FROM Ability WHERE ability_id == :id LIMIT 1")
    suspend fun getPokemonAbilityByIdAsync(id: Int): Ability

    @Update
    suspend fun updatePokemonAbility(abilities: Ability)

    @Delete
    suspend fun deletePokemonAbility(abilities: Ability)

    @Query("DELETE FROM Ability")
    suspend fun deleteAll()

}

