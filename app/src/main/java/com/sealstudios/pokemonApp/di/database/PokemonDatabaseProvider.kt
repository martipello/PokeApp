package com.sealstudios.pokemonApp.di.database

import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
public class PokemonDatabaseProvider {

    @Singleton
    @Provides
    fun providePokemonDatabase(@ApplicationContext application: Application): PokemonRoomDatabase {
        return PokemonRoomDatabase.getDatabase(application, GlobalScope)
    }


}