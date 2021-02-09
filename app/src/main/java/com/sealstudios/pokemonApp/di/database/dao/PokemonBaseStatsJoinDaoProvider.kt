package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonBaseStatsJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonBaseStatsJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonTypeJoinDao(application: Application): PokemonBaseStatsJoinDao {
        return PokemonRoomDatabase.getDatabase(application, GlobalScope).pokemonBaseStatsJoinDao()
    }
}
