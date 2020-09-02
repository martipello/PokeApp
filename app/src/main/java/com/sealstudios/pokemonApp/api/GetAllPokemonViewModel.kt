package com.sealstudios.pokemonApp.api

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class GetAllPokemonViewModel @ViewModelInject constructor(
    private val workManager: WorkManager
) : ViewModel() {

    fun getAllPokemon() {
        workManager.enqueue(OneTimeWorkRequest.from(GetAllPokemonWorkManager::class.java))
    }

}