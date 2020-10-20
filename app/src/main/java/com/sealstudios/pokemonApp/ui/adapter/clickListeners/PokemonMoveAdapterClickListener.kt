package com.sealstudios.pokemonApp.ui.adapter.clickListeners

import android.view.View
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove

interface PokemonMoveAdapterClickListener {
    fun onItemSelected(position: Int, pokemonMove: PokemonMove)
}