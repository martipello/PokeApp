package com.sealstudios.pokemonApp.di.database.repository

import com.sealstudios.pokemonApp.database.dao.TypeDao
import com.sealstudios.pokemonApp.database.dao.TypeJoinDao
import com.sealstudios.pokemonApp.repository.TypeRepository
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
            typeDao: TypeDao,
            typeJoinDao: TypeJoinDao,
    ): TypeRepository {
        return TypeRepository(typeDao, typeJoinDao)
    }

}
