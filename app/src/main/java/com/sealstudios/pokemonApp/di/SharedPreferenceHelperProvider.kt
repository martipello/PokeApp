package com.sealstudios.pokemonApp.di

import android.content.Context
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class SharedPreferenceHelperProvider {

    @Singleton
    @Provides
    fun provideSharedPreferencesHelper(@ApplicationContext context: Context): SharedPreferenceHelper {
        return SharedPreferenceHelper(context)
    }
}