package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.AbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.AbilityMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithAbilitiesAndMetaData
import com.sealstudios.pokemonApp.database.dao.AbilityMetaDataDao
import com.sealstudios.pokemonApp.database.dao.AbilityMetaDataJoinDao
import javax.inject.Inject


class AbilityMetaDataRepository @Inject constructor(
        private val abilityMetaDataDao: AbilityMetaDataDao,
        private val abilityMetaDataJoinDao: AbilityMetaDataJoinDao
) {

    suspend fun insertAbilityMetaData(abilityMetaData: AbilityMetaData) {
        abilityMetaDataDao.insertAbilityMetaData(abilityMetaData)
    }

    suspend fun getPokemonWithAbilitiesAndMetaDataByIdAsync(id: Int): PokemonWithAbilitiesAndMetaData {
        return abilityMetaDataDao.getPokemonWithAbilitiesAndMetaDataByIdAsync(id)
    }

    // ABILITY META DATA JOINS

    suspend fun insertAbilityMetaDataJoin(abilityMetaDataJoin: AbilityMetaDataJoin) {
        abilityMetaDataJoinDao.insertAbilityMetaDataJoin(abilityMetaDataJoin)
    }

}