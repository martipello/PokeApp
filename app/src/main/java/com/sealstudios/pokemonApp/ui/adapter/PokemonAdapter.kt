package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder

class PokemonAdapter(
    private val clickListener: PokemonAdapterClickListener,
    private val glide: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = diffCallback()
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            PokemonViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(
            binding,
            clickListener,
            glide,
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

    fun submitList(list: List<PokemonWithTypesAndSpeciesForList>) {
        differ.submitList(list)
    }

    companion object {

        private fun diffCallback(): DiffUtil.ItemCallback<PokemonWithTypesAndSpeciesForList> {
            return object : DiffUtil.ItemCallback<PokemonWithTypesAndSpeciesForList>() {

                override fun areItemsTheSame(
                    oldItem: PokemonWithTypesAndSpeciesForList,
                    newItem: PokemonWithTypesAndSpeciesForList
                ): Boolean =
                    oldItem.pokemon.id == newItem.pokemon.id

                override fun areContentsTheSame(
                    oldItem: PokemonWithTypesAndSpeciesForList,
                    newItem: PokemonWithTypesAndSpeciesForList
                ): Boolean = oldItem.pokemon.id == newItem.pokemon.id &&
                            oldItem.types.size == newItem.types.size &&
                            oldItem.species?.species == newItem.species?.species
            }
        }
    }


}
