package com.sealstudios.pokemonApp.di.network

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.sealstudios.pokemonApp.BuildConfig
import com.sealstudios.pokemonApp.api.service.PokemonService
import com.sealstudios.pokemonApp.di.network.OkHttpClient.Companion.getOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApolloClientProvider {
    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.builder()
            .serverUrl(BuildConfig.GRAPH_QL_BASE_URL)
            .build()
    }
}

