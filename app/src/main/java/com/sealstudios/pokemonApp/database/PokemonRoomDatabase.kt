package com.sealstudios.pokemonApp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase.Companion.DATABASE_VERSION
import com.sealstudios.pokemonApp.database.`object`.*
import com.sealstudios.pokemonApp.database.`object`.joins.*
import com.sealstudios.pokemonApp.database.dao.*
import com.sealstudios.pokemonApp.util.RoomIntListConverter
import com.sealstudios.pokemonApp.util.RoomStringListConverter

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


    companion object {
        const val DATABASE_VERSION: Int = 1
        private const val DATABASE_NAME: String = "DEX"

        @Volatile
        private var INSTANCE: PokemonRoomDatabase? = null

        fun getDatabase(context: Context): PokemonRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PokemonRoomDatabase::class.java,
                        DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}