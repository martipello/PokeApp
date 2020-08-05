package com.sealstudios.pokemonApp.ui.adapter

import com.sealstudios.pokemonApp.database.`object`.Pokemon

interface ClickListener {
    fun onItemSelected(position: Int, item: Pokemon)
}