package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder


class PokemonPagingDataAdapter(
    private val glide: RequestManager,
    private val clickListener: PokemonAdapterClickListener
) : PagingDataAdapter<PokemonWithTypesAndSpeciesForList, PokemonViewHolder>(diffCallback) {
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder =
        PokemonViewHolder(
            PokemonViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            clickListener,
            glide,
        )

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<PokemonWithTypesAndSpeciesForList>() {
                override fun areItemsTheSame(
                    oldItem: PokemonWithTypesAndSpeciesForList,
                    newItem: PokemonWithTypesAndSpeciesForList
                ): Boolean =
                    oldItem.pokemon.id == newItem.pokemon.id

                override fun areContentsTheSame(
                    oldItem: PokemonWithTypesAndSpeciesForList,
                    newItem: PokemonWithTypesAndSpeciesForList
                ): Boolean =
                    oldItem.types.size == newItem.types.size &&
                            oldItem.species?.species == newItem.species?.species
            }
    }
}