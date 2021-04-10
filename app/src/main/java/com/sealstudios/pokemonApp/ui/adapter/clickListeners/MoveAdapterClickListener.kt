package com.sealstudios.pokemonApp.ui.adapter.clickListeners

import com.sealstudios.pokemonApp.database.`object`.Move

interface MoveAdapterClickListener {
    fun onItemSelected(position: Int, move: Move)
}