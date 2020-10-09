package com.sealstudios.pokemonApp.ui.adapter.clickListeners

import android.view.View
import com.sealstudios.pokemonApp.database.`object`.Pokemon

interface PokemonAdapterClickListener {
    fun onItemSelected(pokemon: Pokemon, view: View)
}