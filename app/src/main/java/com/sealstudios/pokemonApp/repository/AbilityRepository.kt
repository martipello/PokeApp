package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.Ability
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityJoin
import com.sealstudios.pokemonApp.database.dao.AbilityDao
import com.sealstudios.pokemonApp.database.dao.AbilityJoinDao
import javax.inject.Inject


class AbilityRepository @Inject constructor(
        private val abilityDao: AbilityDao,
        private val abilityJoinDao: AbilityJoinDao
) {

    suspend fun insertPokemonAbility(ability: Ability) {
        abilityDao.insertPokemonAbility(ability)
    }

    // ABILITY JOINS

    suspend fun insertPokemonAbilityJoin(abilityJoin: AbilityJoin) {
        abilityJoinDao.insertPokemonAbilityJoin(abilityJoin)
    }

}