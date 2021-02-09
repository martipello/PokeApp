package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.GenerationHeaderViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.GenerationHeader
import java.util.*

class GenerationHeaderViewHolder
constructor(
    private val binding: GenerationHeaderViewHolderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonGenerationName: GenerationHeader) = with(binding) {
        title.text = pokemonGenerationName.headerName.capitalize(Locale.ROOT)
    }

    companion object {
        const val layoutType = 1001
    }

}