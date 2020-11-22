package com.sealstudios.pokemonApp.di.network

import com.sealstudios.pokemonApp.BuildConfig
import com.sealstudios.pokemonApp.api.service.PokemonService
import com.sealstudios.pokemonApp.di.network.OkHttpClient.Companion.getOkHttpClient
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
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient().build())
            .build()
            .create(PokemonService::class.java)
    }
}

