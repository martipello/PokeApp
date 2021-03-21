package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.PokemonTypeMetaDataJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonTypeMetaDataJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): PokemonTypeMetaDataJoinDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonTypeMetaDataJoinDao()
    }
}
