package com.sealstudios.pokemonApp.ui.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.objects.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding

class PokemonViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
    private val binding = PokemonViewHolderBinding.bind(view)

    fun bind(pokemon: Pokemon) {
        with(binding) {
            binding.pokemonNameTextView.text = pokemon.name
            binding.pokemonNameTextView.setOnClickListener {
                Log.d("VH", pokemon.name)
            }
        }
    }


}