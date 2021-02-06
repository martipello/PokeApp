package com.sealstudios.pokemonApp.ui.adapter.clickListeners

import android.view.View
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove

interface AdapterClickListener {
    fun onItemSelected(position: Int)
}