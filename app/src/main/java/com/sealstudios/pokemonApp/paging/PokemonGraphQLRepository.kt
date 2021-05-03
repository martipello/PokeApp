package com.sealstudios.pokemonApp.paging

import androidx.lifecycle.LiveData
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import com.sealstudios.pokemonApp.database.dao.PokemonGraphQLDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PokemonGraphQLRepository @Inject constructor(
    private val pokemonDao: PokemonGraphQLDao
) {

    suspend fun insertPokemonGraphQL(pokemon: PokemonGraphQL) {
        pokemonDao.insertPokemonGraphQL(pokemon)
    }

    suspend fun insertPokemonGraphQL(pokemon: List<PokemonGraphQL>) {
        pokemonDao.insertPokemonGraphQL(pokemon)
    }

    fun searchAndFilterPokemonGraphQL(
        search: String,
        filters: List<String>
    ): LiveData<List<PokemonGraphQL>?> {
        return pokemonDao.searchAndFilterPokemonGraphQL(search, filters)
    }

    suspend fun clearTable(){
        withContext(Dispatchers.Default){
            pokemonDao.deleteAll()
        }
    }
}