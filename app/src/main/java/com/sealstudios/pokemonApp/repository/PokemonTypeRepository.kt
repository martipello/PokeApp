package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.api.`object`.Type
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonTypeRepository @Inject constructor(
        private val pokemonTypeDao: PokemonTypeDao,
        private val pokemonTypeJoinDao: PokemonTypeJoinDao
) {

    suspend fun getPokemonById(id: Int): PokemonWithTypes {
        return pokemonTypeDao.getPokemonWithTypesById(id)
    }

    private suspend fun insertPokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.insertPokemonType(pokemonType)
    }

    private suspend fun updatePokemonType(pokemonType: PokemonType) {
        pokemonTypeDao.updatePokemonType(pokemonType)
    }

    //TYPE JOIN

    private suspend fun insertPokemonTypeJoin(pokemonTypeJoin: PokemonTypesJoin) {
        pokemonTypeJoinDao.insertPokemonTypeJoin(pokemonTypeJoin)
    }

    suspend fun insertPokemonTypes(
            remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {
            remotePokemon.types.map {

                val pokemonType = PokemonType.mapDbPokemonTypeFromPokemonResponse(it)
                val pokemonTypeJoin = PokemonTypesJoin.mapTypeJoinsFromPokemonResponse(remotePokemon.id, it.type.url)

                insertPokemonType(pokemonType)
                insertPokemonTypeJoin(pokemonTypeJoin)
            }
        }
    }

    suspend fun updateType(
            type: Type,
            pokemonId: Int
    ) {
        withContext(Dispatchers.IO) {

            val pokemonType = PokemonType.mapDbPokemonTypeFromTypeResponse(type)
            val pokemonTypeJoin = PokemonTypesJoin.mapTypeJoinFromTypeResponse(pokemonId, type)
            updatePokemonType(pokemonType)
            insertPokemonTypeJoin(pokemonTypeJoin)
        }
    }

}