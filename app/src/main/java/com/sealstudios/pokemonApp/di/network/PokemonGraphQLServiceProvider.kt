package com.sealstudios.pokemonApp.di.network

import android.content.Context
import com.sealstudios.pokemonApp.BuildConfig
import com.sealstudios.pokemonApp.api.service.PokemonGraphQLService
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
class PokemonGraphQLServiceProvider {
    @Provides
    @Singleton
    fun providePokemonGraphQLService(@ApplicationContext context: Context): PokemonGraphQLService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GRAPH_QL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.getOkHttpClient(context).build())
            .build()
            .create(PokemonGraphQLService::class.java)
    }
}