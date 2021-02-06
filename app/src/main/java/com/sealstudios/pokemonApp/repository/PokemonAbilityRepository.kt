package com.sealstudios.pokemonApp.repository

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityJoin
import com.sealstudios.pokemonApp.database.dao.PokemonAbilityDao
import com.sealstudios.pokemonApp.database.dao.PokemonAbilityJoinDao
import javax.inject.Inject


class PokemonAbilityRepository @Inject constructor(
    private val pokemonAbilityDao: PokemonAbilityDao,
    private val pokemonAbilityJoinDao: PokemonAbilityJoinDao
) {

    suspend fun insertPokemonAbility(pokemonAbility: PokemonAbility) {
        pokemonAbilityDao.insertPokemonAbility(pokemonAbility)
    }

    suspend fun insertPokemonAbilities(pokemonAbilities: List<PokemonAbility>) {
        pokemonAbilityDao.insertPokemonAbilities(pokemonAbilities)
    }

    fun getPokemonAbilityById(id: Int): LiveData<PokemonAbility> {
        return pokemonAbilityDao.getPokemonAbilityById(id)
    }

    suspend fun getPokemonAbilityByIdAsync(id: Int): PokemonAbility? {
        return pokemonAbilityDao.getPokemonAbilityByIdAsync(id)
    }

    suspend fun getPokemonAbilitiesByIdsAsync(ids: List<Int>): List<PokemonAbility> {
        return pokemonAbilityDao.getPokemonAbilitiesByIdsAsync(ids)
    }

    suspend fun updatePokemonAbility(pokemonAbility: PokemonAbility) {
        pokemonAbilityDao.updatePokemonAbility(pokemonAbility)
    }

    suspend fun deletePokemonAbility(pokemonAbility: PokemonAbility) {
        pokemonAbilityDao.deletePokemonAbility(pokemonAbility)
    }

    // ABILITY JOINS

    suspend fun insertPokemonAbilityJoin(pokemonAbilityJoin: PokemonAbilityJoin) {
        pokemonAbilityJoinDao.insertPokemonAbilityJoin(pokemonAbilityJoin)
    }

    suspend fun insertPokemonAbilitiesJoins(pokemonAbilitiesJoin: List<PokemonAbilityJoin>) {
        pokemonAbilityJoinDao.insertPokemonAbilitiesJoin(pokemonAbilitiesJoin)
    }

    suspend fun updatePokemonAbilitiesJoin(pokemonAbilityJoin: PokemonAbilityJoin) {
        pokemonAbilityJoinDao.updatePokemonAbilityJoin(pokemonAbilityJoin)
    }

    suspend fun deletePokemonAbilitiesJoin(pokemonAbilityJoin: PokemonAbilityJoin) {
        pokemonAbilityJoinDao.deletePokemonAbilityJoin(pokemonAbilityJoin)
    }

}