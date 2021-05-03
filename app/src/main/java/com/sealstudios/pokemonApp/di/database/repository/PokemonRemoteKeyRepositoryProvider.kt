package com.sealstudios.pokemonApp.di.database.repository

import com.sealstudios.pokemonApp.database.dao.PokemonRemoteKeyDao
import com.sealstudios.pokemonApp.paging.RemoteKeyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PokemonRemoteKeyRepositoryProvider {

    @Singleton
    @Provides
    fun providePokemonRepository(
        pokemnRemoteKeyDao: PokemonRemoteKeyDao
    ): RemoteKeyRepository {
        return RemoteKeyRepository(pokemnRemoteKeyDao)
    }

}
