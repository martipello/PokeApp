package com.sealstudios.pokemonApp.di.database.repository

import com.sealstudios.pokemonApp.database.dao.PokemonTypeJoinDao
import com.sealstudios.pokemonApp.repository.PokemonTypeJoinRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonTypeJoinRepositoryProvider {

    @Singleton
    @Provides
    fun providePokemonTypeRepository(
        pokemonTypeJoinDao: PokemonTypeJoinDao
    ): PokemonTypeJoinRepository {
        return PokemonTypeJoinRepository(pokemonTypeJoinDao)
    }

}
