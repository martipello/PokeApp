package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonTypeMetaData
import com.sealstudios.pokemonApp.database.`object`.joins.PokemonTypeMetaDataJoin
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes

@Dao
interface PokemonTypeMetaDataJoinDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonTypeMetaData(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin)

    @Update
    suspend fun updatePokemonTypeMetaData(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin)

    @Delete
    suspend fun deletePokemonType(pokemonTypeMetaDataJoin: PokemonTypeMetaDataJoin)

    @Query("DELETE FROM PokemonTypeMetaDataJoin")
    suspend fun deleteAll()

}

