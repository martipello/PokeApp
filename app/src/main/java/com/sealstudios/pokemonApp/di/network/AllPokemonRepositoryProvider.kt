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
        pokemonRepository: PokemonRepository,
        pokemonTypeRepository: PokemonTypeRepository,
        pokemonTypeJoinRepository: PokemonTypeJoinRepository,
        pokemonSpeciesRepository: PokemonSpeciesRepository,
        pokemonSpeciesJoinRepository: PokemonSpeciesJoinRepository,
        pokemonMoveRepository: PokemonMoveRepository,
        pokemonMoveJoinRepository: PokemonMoveJoinRepository
    ): GetAllPokemonRepository {
        return GetAllPokemonRepository(
            remotePokemonRepository,
            pokemonRepository,
            pokemonTypeRepository,
            pokemonTypeJoinRepository,
            pokemonSpeciesRepository,
            pokemonSpeciesJoinRepository,
            pokemonMoveRepository,
            pokemonMoveJoinRepository
        )
    }

}