package com.sealstudios.pokemonApp.di.database.repository

import com.sealstudios.pokemonApp.api.PokemonService
import com.sealstudios.pokemonApp.database.dao.PokemonDao
import com.sealstudios.pokemonApp.repository.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonRepositoryProvider {

    @Singleton
    @Provides
    fun providePokemonRepository(
        pokemonDao: PokemonDao,
        pokemonService: PokemonService
    ): PokemonRepository {
        return PokemonRepository(pokemonDao, pokemonService)
    }

}
