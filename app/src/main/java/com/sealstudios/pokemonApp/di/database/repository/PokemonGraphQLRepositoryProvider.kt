package com.sealstudios.pokemonApp.di.database.repository

import com.sealstudios.pokemonApp.database.dao.PokemonGraphQLDao
import com.sealstudios.pokemonApp.paging.PokemonGraphQLRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonGraphQLRepositoryProvider {

    @Singleton
    @Provides
    fun providePokemonRepository(
        pokemonDao: PokemonGraphQLDao
    ): PokemonGraphQLRepository {
        return PokemonGraphQLRepository(pokemonDao)
    }

}
