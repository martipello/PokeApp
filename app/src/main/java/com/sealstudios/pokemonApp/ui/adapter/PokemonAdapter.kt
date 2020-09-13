package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies

class PokemonAdapter(
    private val clickListener: PokemonAdapterClickListener,
    private val glide: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = diffCallback()
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PokemonViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.pokemon_view_holder,
                parent,
                false
            ),
            clickListener,
            glide
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<PokemonWithTypesAndSpecies>) {
        differ.submitList(list)
    }

    companion object {
        private fun diffCallback(): DiffUtil.ItemCallback<PokemonWithTypesAndSpecies> {
            return object : DiffUtil.ItemCallback<PokemonWithTypesAndSpecies>() {

                override fun areItemsTheSame(
                    oldItem: PokemonWithTypesAndSpecies,
                    newItem: PokemonWithTypesAndSpecies
                ): Boolean {
                    return oldItem.pokemon.id == newItem.pokemon.id
                }

                override fun areContentsTheSame(
                    oldItem: PokemonWithTypesAndSpecies,
                    newItem: PokemonWithTypesAndSpecies
                ): Boolean {
                    return oldItem.pokemon.id == newItem.pokemon.id
                }

            }
        }
    }


}

