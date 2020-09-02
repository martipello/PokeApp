package com.sealstudios.pokemonApp.di.database.repository

import com.sealstudios.pokemonApp.api.PokemonService
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import com.sealstudios.pokemonApp.repository.PokemonTypeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonTypeRepositoryProvider {

    @Singleton
    @Provides
    fun providePokemonTypeRepository(
        pokemonTypeDao: PokemonTypeDao
    ): PokemonTypeRepository {
        return PokemonTypeRepository(pokemonTypeDao)
    }

}
