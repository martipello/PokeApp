package com.sealstudios.pokemonApp.ui.adapter

import com.sealstudios.pokemonApp.objects.Pokemon

interface ClickListener {
    fun onItemSelected(position: Int, item: Pokemon)
}