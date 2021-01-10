package com.sealstudios.pokemonApp.ui.adapter.clickListeners

import android.view.View
import com.sealstudios.pokemonApp.database.`object`.PokemonForList

interface PokemonAdapterClickListener {
    fun onItemSelected(pokemon: PokemonForList, view: View)
}