package com.sealstudios.pokemonApp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.DATABASE_VERSION
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.*
import com.sealstudios.pokemonApp.database.dao.*
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import com.sealstudios.pokemonApp.util.RoomStringListConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
        entities = [
            Pokemon::class,
            PokemonType::class,
            PokemonTypesJoin::class,
            PokemonMove::class,
            PokemonMovesJoin::class,
            PokemonSpecies::class,
            PokemonSpeciesJoin::class,
            PokemonMoveMetaData::class,
            PokemonMoveMetaDataJoin::class,
            PokemonAbility::class,
            PokemonAbilityJoin::class,
            PokemonAbilityMetaData::class,
            PokemonAbilityMetaDataJoin::class,
            PokemonBaseStats::class,
            PokemonBaseStatsJoin::class,
            PokemonTypeMetaData::class,
            PokemonTypeMetaDataJoin::class,
        ],
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
    abstract fun pokemonMoveMetaDataDao(): PokemonMoveMetaDataDao
    abstract fun pokemonMoveMetaDataJoinDao(): PokemonMoveMetaDataJoinDao
    abstract fun pokemonSpeciesDao(): PokemonSpeciesDao
    abstract fun pokemonSpeciesJoinDao(): PokemonSpeciesJoinDao
    abstract fun pokemonAbilityDao(): PokemonAbilityDao
    abstract fun pokemonAbilityJoinDao(): PokemonAbilityJoinDao
    abstract fun pokemonAbilityMetaDataDao(): PokemonAbilityMetaDataDao
    abstract fun pokemonAbilityMetaDataJoinDao(): PokemonAbilityMetaDataJoinDao
    abstract fun pokemonBaseStatsDao(): PokemonBaseStatsDao
    abstract fun pokemonBaseStatsJoinDao(): PokemonBaseStatsJoinDao
    abstract fun pokemonTypeMetaDataDao(): PokemonTypeMetaDataDao
    abstract fun pokemonTypeMetaDataJoinDao(): PokemonTypeMetaDataJoinDao

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
                            database.pokemonMoveJoinDao(),
                            database.pokemonMoveMetaDataDao(),
                            database.pokemonMoveMetaDataJoinDao(),
                            database.pokemonAbilityDao(),
                            database.pokemonAbilityJoinDao(),
                            database.pokemonAbilityMetaDataDao(),
                            database.pokemonAbilityMetaDataJoinDao(),
                            database.pokemonBaseStatsDao(),
                            database.pokemonBaseStatsJoinDao(),
                            database.pokemonTypeMetaDataDao(),
                            database.pokemonTypeMetaDataJoinDao()
                    )
                }
            }
        }

        suspend fun populateDatabase(
                pokemonDao: PokemonDao,
                pokemonTypeDao: PokemonTypeDao,
                pokemonTypeJoinDao: PokemonTypeJoinDao,
                pokemonMoveDao: PokemonMoveDao,
                pokemonMoveJoinDao: PokemonMoveJoinDao,
                pokemonMoveMetaDataDao: PokemonMoveMetaDataDao,
                pokemonMoveMetaDataJoinDao: PokemonMoveMetaDataJoinDao,
                pokemonAbilityDao: PokemonAbilityDao,
                pokemonAbilityJoinDao: PokemonAbilityJoinDao,
                pokemonAbilityMetaDataDao: PokemonAbilityMetaDataDao,
                pokemonAbilityMetaDataJoinDao: PokemonAbilityMetaDataJoinDao,
                pokemonBaseStatsDao: PokemonBaseStatsDao,
                pokemonBaseStatsJoinDao: PokemonBaseStatsJoinDao,
                pokemonTypeMetaDataDao: PokemonTypeMetaDataDao,
                pokemonTypeMetaDataJoinDao: PokemonTypeMetaDataJoinDao,
        ) {
            // Delete all content here.
            pokemonDao.deleteAll()
            pokemonTypeDao.deleteAll()
            pokemonTypeJoinDao.deleteAll()
            pokemonMoveDao.deleteAll()
            pokemonMoveJoinDao.deleteAll()
            pokemonMoveMetaDataDao.deleteAll()
            pokemonMoveMetaDataJoinDao.deleteAll()
            pokemonAbilityDao.deleteAll()
            pokemonAbilityJoinDao.deleteAll()
            pokemonAbilityMetaDataDao.deleteAll()
            pokemonAbilityMetaDataJoinDao.deleteAll()
            pokemonBaseStatsDao.deleteAll()
            pokemonBaseStatsJoinDao.deleteAll()
            pokemonTypeMetaDataDao.deleteAll()
            pokemonTypeMetaDataJoinDao.deleteAll()
        }
    }

    companion object {
        const val DATABASE_VERSION: Int = 1
        private const val DATABASE_NAME: String = "DEX"

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