package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.MoveMetaDataJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonMoveMetaDataJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonDao(application: Application): MoveMetaDataJoinDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonMoveMetaDataJoinDao()
    }
}
