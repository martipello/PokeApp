package com.sealstudios.pokemonApp.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.loadCircularImage

class PokemonViewHolder constructor(
    itemView: View,
    private val clickListener: ClickListener?,
    private val requestManager: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = PokemonViewHolderBinding.bind(itemView)

    @SuppressLint("DefaultLocale")
    fun bind(pokemon: Pokemon) = with(binding) {
        binding.pokemonNameTextView.text = pokemon.name.capitalize()
        binding.pokemonSpeciesTextViewLabel.text = pokemon.species.capitalize()
        binding.pokemonTypesTextViewLabel.text = pokemon.types.toString()
        itemView.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, pokemon)
        }
        binding.pokemonImageView.loadCircularImage(
            model = pokemon.url,
            borderSize = 2.0f,
            borderColor = Color.BLUE,
            glide = requestManager,
            listener = null
        )
    }
}



