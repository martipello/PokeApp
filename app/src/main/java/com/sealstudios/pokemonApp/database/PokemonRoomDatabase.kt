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
            Type::class,
            TypesJoin::class,
            Move::class,
            MovesJoin::class,
            Species::class,
            SpeciesJoin::class,
            MoveMetaData::class,
            MoveMetaDataJoin::class,
            Ability::class,
            AbilityJoin::class,
            AbilityMetaData::class,
            AbilityMetaDataJoin::class,
            BaseStats::class,
            BaseStatsJoin::class,
            TypeMetaData::class,
            TypeMetaDataJoin::class,
            EvolutionChain::class,
            EvolutionDetail::class,
            EvolutionDetailJoin::class,
        ],
        version = DATABASE_VERSION,
        exportSchema = false
)
@TypeConverters(RoomStringListConverter::class, RoomIntListConverter::class)
abstract class PokemonRoomDatabase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonTypeDao(): TypeDao
    abstract fun pokemonTypeJoinDao(): TypeJoinDao
    abstract fun pokemonMoveDao(): MoveDao
    abstract fun pokemonMoveJoinDao(): MoveJoinDao
    abstract fun pokemonMoveMetaDataDao(): MoveMetaDataDao
    abstract fun pokemonMoveMetaDataJoinDao(): MoveMetaDataJoinDao
    abstract fun pokemonSpeciesDao(): SpeciesDao
    abstract fun pokemonSpeciesJoinDao(): SpeciesJoinDao
    abstract fun pokemonAbilityDao(): AbilityDao
    abstract fun pokemonAbilityJoinDao(): AbilityJoinDao
    abstract fun pokemonAbilityMetaDataDao(): AbilityMetaDataDao
    abstract fun pokemonAbilityMetaDataJoinDao(): AbilityMetaDataJoinDao
    abstract fun pokemonBaseStatsDao(): BaseStatsDao
    abstract fun pokemonBaseStatsJoinDao(): BaseStatsJoinDao
    abstract fun pokemonTypeMetaDataDao(): TypeMetaDataDao
    abstract fun pokemonTypeMetaDataJoinDao(): TypeMetaDataJoinDao
    abstract fun pokemonEvolutionChainDao(): EvolutionChainDao
    abstract fun pokemonEvolutionDetailDao(): EvolutionDetailDao
    abstract fun pokemonEvolutionDetailJoinDao(): EvolutionDetailJoinDao


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