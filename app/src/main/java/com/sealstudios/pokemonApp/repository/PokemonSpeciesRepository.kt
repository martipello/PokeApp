package com.sealstudios.pokemonApp.repository

import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.dao.PokemonSpeciesDao
import com.sealstudios.pokemonApp.database.dao.PokemonTypeDao
import javax.inject.Inject


class PokemonSpeciesRepository @Inject constructor(
    private val pokemonSpeciesDao: PokemonSpeciesDao
) {

    suspend fun insertPokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.insertSpecies(pokemonSpecies)
    }

    suspend fun insertPokemonSpecies(pokemonSpecies: List<PokemonSpecies>) {
        pokemonSpeciesDao.insertSpecies(pokemonSpecies)
    }

    suspend fun updatePokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.updateSpecies(pokemonSpecies)
    }

    suspend fun deletePokemonSpecies(pokemonSpecies: PokemonSpecies) {
        pokemonSpeciesDao.deleteSpecies(pokemonSpecies)
    }

}