package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.databinding.PokemonTempViewHolderBinding
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonTempViewHolder


class PokemonPagingDataAdapter(
    private val glide: RequestManager,
) : PagingDataAdapter<PokemonWithTypesAndSpeciesForList, PokemonTempViewHolder>(diffCallback) {
    override fun onBindViewHolder(holder: PokemonTempViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonTempViewHolder =
    PokemonTempViewHolder(
        PokemonTempViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    null,
    glide,
    )

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PokemonWithTypesAndSpeciesForList>() {
            override fun areItemsTheSame(oldItem: PokemonWithTypesAndSpeciesForList, newItem: PokemonWithTypesAndSpeciesForList): Boolean =
                oldItem.pokemon.id == newItem.pokemon.id

            override fun areContentsTheSame(oldItem: PokemonWithTypesAndSpeciesForList, newItem: PokemonWithTypesAndSpeciesForList): Boolean =
                oldItem.types.size == newItem.types.size &&
                        oldItem.species?.species == newItem.species?.species
        }
    }
}