package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.PokemonWithAbilitiesAndMetaData
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

    suspend fun insertAbilityMetaDataList(pokemonAbilityMetaData: List<PokemonAbilityMetaData>) {
        pokemonAbilityMetaDataDao.insertAbilityMetaDataList(pokemonAbilityMetaData)
    }

    suspend fun movesForIdsAsync(ids: List<Int>): List<PokemonAbilityMetaData> {
        return pokemonAbilityMetaDataDao.getAbilityMetaDataByIdsAsync(ids)
    }

    suspend fun getPokemonWithAbilitiesAndMetaDataByIdAsync(id: Int): PokemonWithAbilitiesAndMetaData {
        return pokemonAbilityMetaDataDao.getPokemonWithAbilitiesAndMetaDataByIdAsync(id)
    }

    suspend fun updateAbilityMetaData(pokemonAbilityMetaData: PokemonAbilityMetaData) {
        pokemonAbilityMetaDataDao.updateAbilityMetaData(pokemonAbilityMetaData)
    }

    suspend fun deleteAbilityMetaData(pokemonAbilityMetaData: PokemonAbilityMetaData) {
        pokemonAbilityMetaDataDao.deleteAbilityMetaData(pokemonAbilityMetaData)
    }

    // ABILITY META DATA JOINS

    suspend fun insertAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin) {
        pokemonAbilityMetaDataJoinDao.insertAbilityMetaDataJoin(pokemonAbilityMetaDataJoin)
    }

    suspend fun insertAbilityMetaDataJoins(pokemonAbilityMetaDataJoin: List<PokemonAbilityMetaDataJoin>) {
        pokemonAbilityMetaDataJoinDao.insertAbilityMetaDataJoins(pokemonAbilityMetaDataJoin)
    }

    suspend fun updateAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin) {
        pokemonAbilityMetaDataJoinDao.updateAbilityMetaDataJoin(pokemonAbilityMetaDataJoin)
    }

    suspend fun deleteAbilityMetaDataJoin(pokemonAbilityMetaDataJoin: PokemonAbilityMetaDataJoin) {
        pokemonAbilityMetaDataJoinDao.deleteAbilityMetaDataJoin(pokemonAbilityMetaDataJoin)
    }

}