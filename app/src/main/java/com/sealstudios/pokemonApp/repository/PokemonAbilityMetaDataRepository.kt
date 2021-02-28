package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonAbilityMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithAbilitiesAndMetaData
import com.sealstudios.pokemonApp.database.dao.PokemonAbilityMetaDataDao
import com.sealstudios.pokemonApp.database.dao.PokemonAbilityMetaDataJoinDao
import javax.inject.Inject


class PokemonAbilityMetaDataRepository @Inject constructor(
    private val pokemonAbilityMetaDataDao: PokemonAbilityMetaDataDao,
    private val pokemonAbilityMetaDataJoinDao: PokemonAbilityMetaDataJoinDao
) {

    suspend fun insertAbilityMetaData(pokemonAbilityMetaData: PokemonAbilityMetaData) {
        pokemonAbilityMetaDataDao.insertAbilityMetaData(pokemonAbilityMetaData)
    }

    suspend fun getPokemonWithAbilitiesAndMetaDataByIdAsync(id: Int): PokemonWithAbilitiesAndMetaData {
        return pokemonAbilityMetaDataDao.getPokemonWithAbilitiesAndMetaDataByIdAsync(id)
    }

    // ABILITY META DATA JOINS

    suspend fun insertAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin) {
        pokemonAbilityMetaDataJoinDao.insertAbilityMetaDataJoin(pokemonAbilityMetaDataJoin)
    }

}