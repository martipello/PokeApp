package com.sealstudios.pokemonApp.di.database.dao


import android.app.Application
import com.sealstudios.pokemonApp.database.PokemonRoomDatabase
import com.sealstudios.pokemonApp.database.dao.MoveJoinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonMovesJoinDaoProvider {

    @Singleton
    @Provides
    fun providePokemonMovesJoinDao(application: Application): MoveJoinDao {
        return PokemonRoomDatabase.getDatabase(application).pokemonMoveJoinDao()
    }
}
