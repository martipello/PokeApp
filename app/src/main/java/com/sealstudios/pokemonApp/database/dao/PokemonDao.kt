package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithSpecies
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesForList


@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemon(pokemon: Pokemon)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemon(pokemon: List<Pokemon>)

    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemon(): LiveData<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAllPokemon(search: String?): LiveData<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchPokemonWithTypeFilters(search: String?): LiveData<List<Pokemon>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name == :name")
    fun getSinglePokemonByName(name: String?): LiveData<Pokemon>

    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonById(id: Int?): LiveData<Pokemon>

    @Update
    suspend fun updatePokemon(pokemon: Pokemon)

    @Delete
    suspend fun deletePokemon(pokemon: Pokemon)

    @Query("DELETE FROM Pokemon")
    suspend fun deleteAll()

    /// ************************************************************************************* ///
    ///                                 PokemonWithSpecies                                    ///

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_id == :id LIMIT 1")
    suspend fun getSinglePokemonWithSpecies(id: Int?): PokemonWithSpecies

    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithSpecies(): LiveData<List<PokemonWithSpecies>>

    ///                                                                                       ///
    /// ************************************************************************************* ///


    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpecies                            ///

    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithTypesAndSpecies(): LiveData<List<PokemonWithTypesAndSpecies>>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonWithTypesAndSpeciesById(id: Int?): LiveData<PokemonWithTypesAndSpecies>

    @Transaction
    @Query("SELECT pokemon_id, pokemon_name, pokemon_image_url, pokemon_sprite FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchPokemonWithTypesAndSpecies(search: String): LiveData<List<PokemonWithTypesAndSpeciesForList>?>

    @Query(
        """SELECT Pokemon.pokemon_id, Pokemon.pokemon_name, Pokemon.pokemon_image_url, Pokemon.pokemon_sprite FROM Pokemon 
                     INNER JOIN PokemonTypesJoin 
                     ON Pokemon.pokemon_id = PokemonTypesJoin.pokemon_id 
                     INNER JOIN PokemonType 
                     ON PokemonType.type_id = PokemonTypesJoin.type_id 
                     WHERE pokemon_name LIKE :search AND type_name IN (:filters)
                     GROUP BY Pokemon.pokemon_id, Pokemon.pokemon_name
                     ORDER BY count(*) DESC, Pokemon.pokemon_id ASC"""
    )
    fun searchAndFilterPokemonWithTypesAndSpecies(
        search: String,
        filters: List<String>
    ): LiveData<List<PokemonWithTypesAndSpeciesForList>?>

    ///                                                                                       ///
    /// ************************************************************************************* ///

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpeciesPaging                      ///

    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithTypesAndSpeciesForPaging(): PagingSource<Int, PokemonWithTypesAndSpecies>

    @Transaction
    @Query("SELECT pokemon_id, pokemon_name, pokemon_image_url, pokemon_sprite FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchPokemonWithTypesAndSpeciesForPaging(search: String): PagingSource<Int, PokemonWithTypesAndSpeciesForList>

    @Query(
        """SELECT Pokemon.pokemon_id, Pokemon.pokemon_name, Pokemon.pokemon_image_url, Pokemon.pokemon_sprite FROM Pokemon 
                     INNER JOIN PokemonTypesJoin 
                     ON Pokemon.pokemon_id = PokemonTypesJoin.pokemon_id 
                     INNER JOIN PokemonType 
                     ON PokemonType.type_id = PokemonTypesJoin.type_id 
                     WHERE pokemon_name LIKE :search AND type_name IN (:filters)
                     GROUP BY Pokemon.pokemon_id, Pokemon.pokemon_name
                     ORDER BY count(*) DESC, Pokemon.pokemon_id ASC"""
    )
    fun searchAndFilterPokemonWithTypesAndSpeciesForPaging(
        search: String,
        filters: List<String>
    ): PagingSource<Int, PokemonWithTypesAndSpeciesForList>

    ///                                                                                       ///
    /// ************************************************************************************* ///

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpeciesAndMoves                    ///

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonWithTypesAndSpeciesAndMovesById(id: Int?): LiveData<PokemonWithTypesAndSpeciesAndMoves>

    ///                                                                                       ///
    /// ************************************************************************************* ///

    /// ************************************************************************************* ///
    ///                                      TESTING QUERIES                                  ///

    @RawQuery(observedEntities = [PokemonWithTypesAndSpecies::class])
    fun pokemonRawQuery(query: SupportSQLiteQuery): LiveData<List<PokemonWithTypesAndSpecies>>

    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchPokemon(search: String): LiveData<List<PokemonWithTypesAndSpecies>>

    @Query(
        """SELECT * FROM Pokemon 
                     INNER JOIN PokemonTypesJoin 
                     ON Pokemon.pokemon_id = PokemonTypesJoin.pokemon_id 
                     INNER JOIN PokemonType 
                     ON PokemonType.type_id = PokemonTypesJoin.type_id 
                     WHERE pokemon_name LIKE :search AND type_name IN (:filters)
                     GROUP BY Pokemon.pokemon_id, Pokemon.pokemon_name
                     ORDER BY count(*) DESC, pokemon_id ASC"""
    )
    fun searchAndFilterPokemon(
        search: String,
        filters: List<String>
    ): LiveData<List<PokemonWithTypesAndSpecies>>

    ///                                                                                       ///
    /// ************************************************************************************* ///

}
