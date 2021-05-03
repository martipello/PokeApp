package com.sealstudios.pokemonApp.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding

class PokemonGraphQLPagingAdapter(
    private val clickListener: PokemonPagingAdapterClickListener,
    private val glide: RequestManager
) : PagingDataAdapter<PokemonGraphQL, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<PokemonGraphQL>() {
            override fun areItemsTheSame(oldItem: PokemonGraphQL, newItem: PokemonGraphQL): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: PokemonGraphQL, newItem: PokemonGraphQL): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonGraphQLViewHolder -> {
                getItem(position)?.let { holder.bind(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            PokemonViewHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PokemonGraphQLViewHolder(
            binding,
            clickListener,
            glide,
        )
    }

}