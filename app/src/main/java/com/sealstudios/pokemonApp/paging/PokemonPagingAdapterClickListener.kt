package com.sealstudios.pokemonApp.paging

import android.view.View
import com.sealstudios.pokemonApp.database.`object`.PokemonForList
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL

interface PokemonPagingAdapterClickListener {
    fun onItemSelected(pokemon: PokemonGraphQL, view: View)
}