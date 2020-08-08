package com.sealstudios.pokemonApp.di.network

import com.sealstudios.pokemonApp.api.PokemonService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonServiceProvider {
    @Provides
    @Singleton
    fun providePokemonService(): PokemonService {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonService::class.java)
    }
}