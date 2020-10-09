package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener

class PokemonMoveViewHolder
constructor(
    private val binding: PokemonMoveViewHolderBinding,
    private val clickListener: PokemonMoveAdapterClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonMove: PokemonMove) = with(binding) {
        this.root.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, pokemonMove)
        }
        pokemonMoveNameTextView.text = pokemonMove.name
    }
}