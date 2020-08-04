package com.sealstudios.pokemonApp.ui.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.data.Pokemon

class PokemonViewHolder constructor(itemView: View,
                                    private val clickListener: ClickListener?) :
        RecyclerView.ViewHolder(itemView) {
    private val binding = PokemonViewHolderBinding.bind(itemView)

    fun bind(item: Pokemon) = with(binding) {
        binding.pokemonNameTextView.text = item.name
        binding.pokemonNameTextView.setOnClickListener {
            Log.d("VH", item.name)
        }

        binding.pokemonNameTextView.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, item)
        }
    }
}

