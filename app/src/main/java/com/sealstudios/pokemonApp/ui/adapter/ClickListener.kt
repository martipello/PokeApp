package com.sealstudios.pokemonApp.ui.adapter

import com.sealstudios.pokemonApp.data.Pokemon

interface ClickListener {
    fun onItemSelected(position: Int, item: Pokemon)
}