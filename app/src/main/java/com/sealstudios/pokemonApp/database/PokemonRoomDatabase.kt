package com.sealstudios.pokemonApp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.DATABASE_VERSION
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonTypesJoin
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Pokemon::class, PokemonType::class, PokemonTypesJoin::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class PokemonRoomDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonTypeDao(): PokemonTypeDao
    abstract fun pokemonTypeJoinDao(): PokemonTypeJoinDao

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
                        database.pokemonTypeJoinDao()
                    )
                }
            }
        }

        suspend fun populateDatabase(
            pokemonDao: PokemonDao,
            pokemonTypeDao: PokemonTypeDao,
            pokemonTypeJoinDao: PokemonTypeJoinDao
        ) {
            // Delete all content here.
            pokemonDao.deleteAll()
            pokemonTypeDao.deleteAll()
            pokemonTypeJoinDao.deleteAll()
        }
    }

    companion object {
        const val DATABASE_VERSION: Int = 1
        private const val DATABASE_NAME: String = "DEX"
        const val POKEMON_TABLE_NAME: String = "pokemon_table"
        const val POKEMON_TYPE_TABLE_NAME: String = "pokemon_type_table"
        const val POKEMON_TYPES_JOIN_TABLE_NAME: String = "pokemon_types_join_table"

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