package com.sealstudios.pokemonApp.ui.adapter

import com.sealstudios.pokemonApp.database.`object`.Pokemon

interface AdapterClickListener {
    fun onItemSelected(position: Int, item: Pokemon)
}