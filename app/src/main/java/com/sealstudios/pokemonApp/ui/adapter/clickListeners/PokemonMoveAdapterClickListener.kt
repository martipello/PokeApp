package com.sealstudios.pokemonApp.ui.adapter.clickListeners

import com.sealstudios.pokemonApp.database.`object`.PokemonMove

interface PokemonMoveAdapterClickListener {
    fun onItemSelected(position: Int, pokemonMove: PokemonMove)
}