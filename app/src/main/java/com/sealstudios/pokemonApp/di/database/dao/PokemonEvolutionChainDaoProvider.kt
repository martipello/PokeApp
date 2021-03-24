package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonEvolutionChainDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonEvolutionChainDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): PokemonEvolutionChainDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonEvolutionChainDao()
    }
}
