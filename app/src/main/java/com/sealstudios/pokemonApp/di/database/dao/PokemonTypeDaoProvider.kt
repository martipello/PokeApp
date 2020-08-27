package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonTypeDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): PokemonTypeDao {
        return PokemonRoomDatabase.getDatabase(application, GlobalScope).pokemonTypeDao()
    }
}
