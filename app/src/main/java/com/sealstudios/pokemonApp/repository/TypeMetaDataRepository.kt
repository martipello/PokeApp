package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.database.`object`.TypeMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.TypeMetaDataJoin
import com.sealstudios.pokemonApp.database.dao.TypeMetaDataDao
import com.sealstudios.pokemonApp.database.dao.TypeMetaDataJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class TypeMetaDataRepository @Inject constructor(
        private val typeMetaDataDao: TypeMetaDataDao,
        private val typeMetaDataJoinDao: TypeMetaDataJoinDao
) {

    private suspend fun insertPokemonTypeMetaData(typeMetaData: TypeMetaData) {
        typeMetaDataDao.insertPokemonTypeMetaData(typeMetaData)
    }

    // TYPE META DATA JOIN

    private suspend fun insertPokemonTypeMetaDataJoin(typeMetaDataJoin: TypeMetaDataJoin) {
        typeMetaDataJoinDao.insertPokemonTypeMetaData(typeMetaDataJoin)
    }


    suspend fun insertPokemonTypeMetaData(
            remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {

            val pokemonTypeMetaData = TypeMetaData.mapRemotePokemonToPokemonTypeMetaData(remotePokemon.id, remotePokemon.types)
            val pokemonTypesMetaDataJoin = TypeMetaDataJoin.mapTypeMetaDataJoinFromPokemonResponse(remotePokemon.id, remotePokemon.types)

            insertPokemonTypeMetaData(
                    pokemonTypeMetaData
            )
            insertPokemonTypeMetaDataJoin(
                    pokemonTypesMetaDataJoin
            )

        }
    }


}