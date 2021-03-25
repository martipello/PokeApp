package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.EvolutionDetailJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonEvolutionDetailJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): EvolutionDetailJoinDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonEvolutionDetailJoinDao()
    }
}
