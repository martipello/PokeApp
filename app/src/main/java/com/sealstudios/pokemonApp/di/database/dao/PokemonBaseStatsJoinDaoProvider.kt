package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.BaseStatsJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonBaseStatsJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonTypeJoinDao(application: Application): BaseStatsJoinDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonBaseStatsJoinDao()
    }
}
