package com.sealstudios.pokemonApp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.DATABASE_VERSION
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Pokemon::class], version = DATABASE_VERSION, exportSchema = false)
abstract class PokemonRoomDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao

    private class PokemonRoomDatabaseCallback (
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.pokemonDao())
                }
            }
        }

        suspend fun populateDatabase(pokemonDao: PokemonDao) {
            // Delete all content here.
            pokemonDao.deleteAll()
        }
    }

    companion object {
        const val DATABASE_VERSION: Int = 1
        const val TABLE_NAME: String = "pokemon_table"
        //Singleton
        @Volatile
        private var INSTANCE: PokemonRoomDatabase? = null

        fun getDatabase(context: Context,  scope: CoroutineScope): PokemonRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PokemonRoomDatabase::class.java,
                        TABLE_NAME
                ).addCallback(PokemonRoomDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}