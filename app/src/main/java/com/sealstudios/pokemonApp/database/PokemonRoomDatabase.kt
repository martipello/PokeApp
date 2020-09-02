package com.sealstudios.pokemonApp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.DATABASE_VERSION
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.dao.*
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import com.sealstudios.pokemonApp.util.RoomStringListConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Pokemon::class,
        PokemonType::class,
        PokemonTypesJoin::class,
        PokemonMove::class,
        PokemonMovesJoin::class,
        PokemonSpecies::class,
        PokemonSpeciesJoin::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(RoomStringListConverter::class, RoomIntListConverter::class)
abstract class PokemonRoomDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonTypeDao(): PokemonTypeDao
    abstract fun pokemonTypeJoinDao(): PokemonTypeJoinDao
    abstract fun pokemonMoveDao(): PokemonMoveDao
    abstract fun pokemonMoveJoinDao(): PokemonMoveJoinDao
    abstract fun pokemonSpeciesDao(): PokemonSpeciesDao
    abstract fun pokemonSpeciesJoinDao(): PokemonSpeciesJoinDao

    private class PokemonRoomDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.pokemonDao(),
                        database.pokemonTypeDao(),
                        database.pokemonTypeJoinDao(),
                        database.pokemonMoveDao(),
                        database.pokemonMoveJoinDao()
                    )
                }
            }
        }

        suspend fun populateDatabase(
            pokemonDao: PokemonDao,
            pokemonTypeDao: PokemonTypeDao,
            pokemonTypeJoinDao: PokemonTypeJoinDao,
            pokemonMoveDao: PokemonMoveDao,
            pokemonMoveJoinDao: PokemonMoveJoinDao
        ) {
            // Delete all content here.
            pokemonDao.deleteAll()
            pokemonTypeDao.deleteAll()
            pokemonTypeJoinDao.deleteAll()
            pokemonMoveDao.deleteAll()
            pokemonMoveJoinDao.deleteAll()
        }
    }

    companion object {
        const val DATABASE_VERSION: Int = 1
        private const val DATABASE_NAME: String = "DEX"
        const val POKEMON_TABLE_NAME: String = "pokemon_table"
        const val POKEMON_TYPE_TABLE_NAME: String = "pokemon_type_table"
        const val POKEMON_MOVE_TABLE_NAME: String = "pokemon_move_table"
        const val POKEMON_WITH_TYPES_TABLE_NAME: String = "pokemon_with_types_table"
        const val POKEMON_TYPES_JOIN_TABLE_NAME: String = "pokemon_types_join_table"
        const val POKEMON_MOVES_JOIN_TABLE_NAME: String = "pokemon_moves_join_table"

        //Singleton
        @Volatile
        private var INSTANCE: PokemonRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PokemonRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonRoomDatabase::class.java,
                    DATABASE_NAME
                ).addCallback(PokemonRoomDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}