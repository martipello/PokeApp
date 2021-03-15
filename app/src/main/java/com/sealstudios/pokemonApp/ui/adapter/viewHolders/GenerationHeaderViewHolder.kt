package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.GenerationHeaderViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.GenerationHeader
import com.sealstudios.pokemonApp.util.extensions.capitalize

class GenerationHeaderViewHolder
constructor(
        private val binding: GenerationHeaderViewHolderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonGenerationName: GenerationHeader) = with(binding) {
        title.text = pokemonGenerationName.headerName.capitalize()
    }

    companion object {
        const val layoutType = 1001
    }

}