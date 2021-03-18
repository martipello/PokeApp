package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonTypeJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonTypeJoinDao(application: Application): PokemonTypeJoinDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonTypeJoinDao()
    }
}
