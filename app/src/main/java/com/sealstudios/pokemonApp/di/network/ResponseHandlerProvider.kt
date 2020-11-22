package com.sealstudios.pokemonApp.di.network

import com.sealstudios.pokemonApp.api.`object`.ResponseHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class ResponseHandlerProvider {

    @Singleton
    @Provides
    fun provideResponseHandler(): ResponseHandler {
        return ResponseHandler()
    }
}