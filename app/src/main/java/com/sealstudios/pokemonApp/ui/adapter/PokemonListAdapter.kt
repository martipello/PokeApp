package com.sealstudios.pokemonApp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.objects.Pokemon
import com.sealstudios.pokemonApp.R

class PokemonListAdapter internal constructor(private val context: Context) : RecyclerView.Adapter<PokemonViewHolder>() {

    private var pokemonList = emptyList<Pokemon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder = PokemonViewHolder(
            LayoutInflater.from(context).inflate(R.layout.pokemon_view_holder, parent, false)
    )

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(pokemonList[position])
    }

    internal fun refreshPokemon(pokemonList: List<Pokemon>) {
        //TODO make this refresh with diff utils
        this.pokemonList = pokemonList
        notifyDataSetChanged()
    }

}

