package com.sealstudios.pokemonApp.paging

import com.sealstudios.pokemonApp.database.`object`.PokemonRemoteKey
import com.sealstudios.pokemonApp.database.dao.PokemonRemoteKeyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteKeyRepository@Inject constructor(
    private val pokemonRemoteKeyDao: PokemonRemoteKeyDao
) {

    suspend fun insertKey(key: PokemonRemoteKey) {
        withContext(Dispatchers.Default){
            pokemonRemoteKeyDao.insert(key)
        }
    }

    suspend fun insertKey(key: List<PokemonRemoteKey>) {
        withContext(Dispatchers.Default){
            pokemonRemoteKeyDao.insert(key)
        }
    }

    suspend fun remoteKey(id: Int): PokemonRemoteKey {
        return pokemonRemoteKeyDao.remoteKeyById(id)
    }

    suspend fun deleteKey(id: Int){
        withContext(Dispatchers.Default){
            pokemonRemoteKeyDao.remoteKeyById(id)
        }
    }

    suspend fun clearTable(){
        withContext(Dispatchers.Default){
            pokemonRemoteKeyDao.deleteAll()
        }
    }
}