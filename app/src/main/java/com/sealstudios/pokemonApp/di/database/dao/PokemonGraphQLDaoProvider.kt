package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonGraphQLDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonGraphQLDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): PokemonGraphQLDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonGraphQLDao()
    }
}
