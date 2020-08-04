package com.sealstudios.pokemonApp.hilt.database.repositories;


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import com.sealstudios.pokemonApp.database.repository.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
public class PokemonRepositoryModule {

    @Singleton
    @Provides
    fun providePokemonRepository(pokemonDao: PokemonDao): PokemonRepository {
        return PokemonRepository(pokemonDao)
    }


}
