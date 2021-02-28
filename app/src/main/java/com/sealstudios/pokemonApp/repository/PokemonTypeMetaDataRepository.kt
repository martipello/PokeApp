package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonTypeMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypeMetaDataJoin
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeMetaDataDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeMetaDataJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonTypeMetaDataRepository @Inject constructor(
        private val pokemonTypeMetaDataDao: PokemonTypeMetaDataDao,
        private val pokemonTypeMetaDataJoinDao: PokemonTypeMetaDataJoinDao
) {

    private suspend fun insertPokemonTypeMetaData(pokemonTypeMetaData: PokemonTypeMetaData) {
        pokemonTypeMetaDataDao.insertPokemonTypeMetaData(pokemonTypeMetaData)
    }

    // TYPE META DATA JOIN

    private suspend fun insertPokemonTypeMetaDataJoin(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin) {
        pokemonTypeMetaDataJoinDao.insertPokemonTypeMetaData(pokemonTypeMetaDataJoin)
    }


    suspend fun insertPokemonTypeMetaData(
            remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {

            val pokemonTypeMetaData = PokemonTypeMetaData.mapRemotePokemonToPokemonTypeMetaData(remotePokemon.id, remotePokemon.types)
            val pokemonTypesMetaDataJoin = PokemonTypeMetaDataJoin.mapTypeMetaDataJoinFromPokemonResponse(remotePokemon.id, remotePokemon.types)

            insertPokemonTypeMetaData(
                    pokemonTypeMetaData
            )
            insertPokemonTypeMetaDataJoin(
                    pokemonTypesMetaDataJoin
            )

        }
    }


}