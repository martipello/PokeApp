package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.api.`object`.ApiPokemon
import com.sealstudios.pokemonApp.database.`object`.Type
import com.sealstudios.pokemonApp.database.`object`.joins.TypesJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes
import com.sealstudios.pokemonApp.database.dao.TypeDao
import com.sealstudios.pokemonApp.database.dao.TypeJoinDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class TypeRepository @Inject constructor(
        private val typeDao: TypeDao,
        private val typeJoinDao: TypeJoinDao
) {

    suspend fun getPokemonById(id: Int): PokemonWithTypes {
        return typeDao.getPokemonWithTypesById(id)
    }

    private suspend fun insertPokemonType(type: com.sealstudios.pokemonApp.database.`object`.Type) {
        typeDao.insertPokemonType(type)
    }

    private suspend fun updatePokemonType(type: com.sealstudios.pokemonApp.database.`object`.Type) {
        typeDao.updatePokemonType(type)
    }

    //TYPE JOIN

    private suspend fun insertPokemonTypeJoin(typeJoin: TypesJoin) {
        typeJoinDao.insertPokemonTypeJoin(typeJoin)
    }

    suspend fun insertPokemonTypes(
            remotePokemon: ApiPokemon
    ) {
        withContext(Dispatchers.IO) {
            remotePokemon.types.map {

                val pokemonType = Type.mapDbPokemonTypeFromPokemonResponse(it)
                val pokemonTypeJoin = TypesJoin.mapTypeJoinsFromPokemonResponse(remotePokemon.id, it.type?.url)

                insertPokemonType(pokemonType)
                insertPokemonTypeJoin(pokemonTypeJoin)
            }
        }
    }

    suspend fun updateType(
            type: com.sealstudios.pokemonApp.api.`object`.Type,
            pokemonId: Int
    ) {
        withContext(Dispatchers.IO) {

            val pokemonType = Type.mapDbPokemonTypeFromTypeResponse(type)
            val pokemonTypeJoin = TypesJoin.mapTypeJoinFromTypeResponse(pokemonId, type)
            updatePokemonType(pokemonType)
            insertPokemonTypeJoin(pokemonTypeJoin)
        }
    }

}