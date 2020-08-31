package com.sealstudios.pokemonApp.di.network

import com.sealstudios.pokemonApp.api.GetAllPokemonRepository
import com.sealstudios.pokemonApp.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AllPokemonRepositoryProvider {

    @Singleton
    @Provides
    fun provideAllPokemonRepository(
        remotePokemonRepository: RemotePokemonRepository,
        localPokemonRepository: PokemonRepository,
        localPokemonTypeRepository: PokemonTypeRepository,
        pokemonTypeJoinRepository: PokemonTypeJoinRepository,
        localPokemonSpeciesRepository: PokemonSpeciesRepository,
        pokemonSpeciesJoinRepository: PokemonSpeciesJoinRepository
    ): GetAllPokemonRepository {
        return GetAllPokemonRepository(
            remotePokemonRepository,
            localPokemonRepository,
            localPokemonTypeRepository,
            pokemonTypeJoinRepository,
            localPokemonSpeciesRepository,
            pokemonSpeciesJoinRepository
        )
    }

}