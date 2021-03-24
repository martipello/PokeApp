package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonEvolutionDetailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonEvolutionDetailDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): PokemonEvolutionDetailDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonEvolutionDetailDao()
    }
}
