package com.sealstudios.pokemonApp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding

class PokemonViewHolder constructor(itemView: View,
                                    private val clickListener: ClickListener?,
                                    private val requestManager: RequestManager?) :
        RecyclerView.ViewHolder(itemView) {
    private val binding = PokemonViewHolderBinding.bind(itemView)

    fun bind(item: Pokemon) = with(binding) {
        binding.pokemonNameTextView.text = item.name
        binding.pokemonNameTextView.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, item)
        }
        requestManager?.load(item.url)?.into(binding.pokemonImageView)
    }
}

