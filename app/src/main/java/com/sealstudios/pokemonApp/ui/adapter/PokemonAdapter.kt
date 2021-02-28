package com.sealstudios.pokemonApp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.MyNativeAd
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.database.`object`.objectInterface.PokemonAdapterListItem
import com.sealstudios.pokemonApp.databinding.AdLayoutBinding
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapterListItemType.Companion.getPokemonAdapterListItemType
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.AdViewHolder
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder

class PokemonAdapter(
        private val clickListener: PokemonAdapterClickListener,
        private val glide: RequestManager
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = diffCallback()
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is MyNativeAd -> {
                PokemonAdapterListItemType.VIEW_TYPE_AD.type
            }
            is PokemonWithTypesAndSpeciesForList -> PokemonAdapterListItemType.VIEW_TYPE_POKEMON.type
            else -> PokemonAdapterListItemType.VIEW_TYPE_POKEMON.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (getPokemonAdapterListItemType(viewType)) {
            PokemonAdapterListItemType.VIEW_TYPE_AD -> {
                val binding =
                        AdLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AdViewHolder(
                        binding,
                )
            }
            PokemonAdapterListItemType.VIEW_TYPE_POKEMON -> {
                val binding =
                        PokemonViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PokemonViewHolder(
                        binding,
                        clickListener,
                        glide,
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonViewHolder -> {
                holder.bind(differ.currentList[position] as PokemonWithTypesAndSpeciesForList)
            }
            is AdViewHolder -> {
                holder.bind((differ.currentList[position] as MyNativeAd).ad)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<PokemonAdapterListItem>) {
        differ.submitList(list)
    }

    companion object {

        private fun diffCallback(): DiffUtil.ItemCallback<PokemonAdapterListItem> {
            return object : DiffUtil.ItemCallback<PokemonAdapterListItem>() {

                override fun areItemsTheSame(
                        oldItem: PokemonAdapterListItem,
                        newItem: PokemonAdapterListItem
                ): Boolean = if (areTheSameType(oldItem, newItem))
                    (oldItem as PokemonWithTypesAndSpeciesForList).pokemon.id == (newItem as PokemonWithTypesAndSpeciesForList).pokemon.id
                else true

                override fun areContentsTheSame(
                        oldItem: PokemonAdapterListItem,
                        newItem: PokemonAdapterListItem
                ): Boolean = if (areTheSameType(oldItem, newItem))
                    (oldItem as PokemonWithTypesAndSpeciesForList).pokemon.id == (newItem as PokemonWithTypesAndSpeciesForList).pokemon.id
                            && oldItem.types.size == newItem.types.size
                            && oldItem.species?.species == newItem.species?.species else true

                fun areTheSameType(oldItem: PokemonAdapterListItem, newItem: PokemonAdapterListItem): Boolean {
                    return oldItem is PokemonWithTypesAndSpeciesForList && newItem is PokemonWithTypesAndSpeciesForList
                }
            }
        }
    }
}


enum class PokemonAdapterListItemType(val type: Int) {
    VIEW_TYPE_POKEMON(type = 0),
    VIEW_TYPE_AD(type = 1);

    companion object {
        @SuppressLint("DefaultLocale")
        fun getPokemonAdapterListItemType(type: Int): PokemonAdapterListItemType {
            return if (type == 1) {
                VIEW_TYPE_AD
            } else {
                VIEW_TYPE_POKEMON
            }
        }
    }
}
