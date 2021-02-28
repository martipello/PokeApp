package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonAbility
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonAbilityJoin
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

    // ABILITY JOINS

    suspend fun insertPokemonAbilityJoin(pokemonAbilityJoin: PokemonAbilityJoin) {
        pokemonAbilityJoinDao.insertPokemonAbilityJoin(pokemonAbilityJoin)
    }

}