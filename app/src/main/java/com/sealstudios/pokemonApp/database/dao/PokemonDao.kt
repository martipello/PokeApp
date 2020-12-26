package com.sealstudios.pokemonApp.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.sealstudios.pokemonApp.database.`object`.*


@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: Pokemon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
    /// ************************************************************************************* ///

    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithSpecies(): LiveData<List<PokemonWithSpecies>>

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypes                                      ///
    /// ************************************************************************************* ///

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_id == :id LIMIT 1")
    fun getSinglePokemonWithTypesById(id: Int?): LiveData<PokemonWithTypes>

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun getPokemonWithTypes(search: String?): LiveData<List<PokemonWithTypes>>

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpecies                            ///
    /// ************************************************************************************* ///

    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithTypesAndSpecies(): LiveData<List<PokemonWithTypesAndSpecies>>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonWithTypesAndSpeciesById(id: Int?): LiveData<PokemonWithTypesAndSpecies>

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAllPokemonWithTypesAndSpecies(search: String): LiveData<List<PokemonWithTypesAndSpecies>>




    /// ************************************************************************************* ///
    ///                                      TESTING QUERIES                                  ///

    @RawQuery(observedEntities = [PokemonWithTypesAndSpecies::class])
    fun pokemonRawQuery(query: SupportSQLiteQuery): LiveData<List<PokemonWithTypesAndSpecies>>

    @Query("SELECT * FROM Pokemon LEFT JOIN PokemonType ON pokemon_id = type_id AND type_name =:filter WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAndFilterAllPokemonWithTypesAndSpecies(search: String, filter: String): LiveData<List<PokemonWithTypesAndSpecies>>


//    @Query("SELECT * FROM Pokemon LEFT JOIN PokemonType WHERE pokemon_name LIKE :search AND type_name IN (:filters) ORDER BY pokemon_id ASC, type_slot DESC")

    //    @Query("SELECT ATTRIBUTES.* FROM ATTRIBUTES INNER JOIN PRODUCTS_ATTRIBUTES
    //    ON PRODUCTS_ATTRIBUTES._ATTRIBUTE_ID = ATTRIBUTES._ID INNER JOIN PRODUCTS
    //    ON PRODUCTS._ID = PRODUCTS_ATTRIBUTES._PRODUCT_ID WHERE PRODUCTS._ID = :productId
    //    ORDER BY PRODUCTS_ATTRIBUTES.DISPLAY_ORDERING ASC")

//    @Query("SELECT Pokemon.* FROM Pokemon INNER JOIN PokemonType ON PokemonType.type_id = type_id INNER JOIN Pokemon ON Pokemon.pokemon_id = pokemon_id WHERE pokemon_name LIKE :search AND PokemonType.type_name IN (:filters) ORDER BY PokemonType.type_slot ASC")

    @Query("SELECT * FROM Pokemon LEFT JOIN PokemonType WHERE pokemon_name LIKE :search AND type_name IN (:filters) ORDER BY pokemon_id ASC, type_slot DESC")
    fun searchAndFilterPokemonWithTypesAndSpeciesOrderedByMatchesAndIds(search: String, filters: List<String>): LiveData<List<PokemonWithTypesAndSpecies>>

    @Query("SELECT * FROM Pokemon INNER JOIN PokemonSpecies, PokemonSpeciesJoin ON Pokemon.pokemon_id = PokemonSpeciesJoin.pokemon_id AND PokemonSpecies.species_id = PokemonSpeciesJoin.species_id ORDER BY species_id ASC")
    fun getAllPokemonSortedBySpecies(): LiveData<List<PokemonWithTypesAndSpecies>>

    @Query("SELECT * FROM Pokemon INNER JOIN PokemonType, PokemonTypesJoin ON Pokemon.pokemon_id = PokemonTypesJoin.type_id AND PokemonType.type_id = PokemonTypesJoin.type_id ORDER BY type_slot ASC")
    fun getAllPokemonSortedByTypeSlot(): LiveData<List<PokemonWithTypesAndSpecies>>

    @Query("SELECT * FROM Pokemon INNER JOIN PokemonType, PokemonTypesJoin ON Pokemon.pokemon_id = PokemonTypesJoin.type_id AND PokemonType.type_id = PokemonTypesJoin.type_id WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAndFilterPokemon(search: String): LiveData<List<PokemonWithTypesAndSpecies>>


    ///                                      TESTING QUERIES                                  ///
    /// ************************************************************************************* ///





    @Transaction
    @Query("SELECT * FROM Pokemon ORDER BY pokemon_id ASC")
    fun getAllPokemonWithTypesAndSpeciesWithPaging(): PagingSource<Int, PokemonWithTypesAndSpecies>

    @Transaction
    @Query("SELECT pokemon_id, pokemon_name, pokemon_image_url, pokemon_sprite FROM Pokemon WHERE pokemon_name LIKE :search ORDER BY pokemon_id ASC")
    fun searchAllPokemonWithTypesAndSpeciesWithPaging(search: String): PagingSource<Int, PokemonWithTypesAndSpeciesForList>

    /// ************************************************************************************* ///
    ///                                 PokemonWithTypesAndSpeciesAndMoves                    ///

    @Transaction
    @Query("SELECT * FROM Pokemon WHERE pokemon_id == :id")
    fun getSinglePokemonWithTypesAndSpeciesAndMovesById(id: Int?): LiveData<PokemonWithTypesAndSpeciesAndMoves>

}
