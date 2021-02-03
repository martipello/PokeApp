package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonAbilityViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonAbilityViewHolder

class PokemonAbilityAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = itemCallback()
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            PokemonAbilityViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonAbilityViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            when (holder) {
                is PokemonAbilityViewHolder -> {
                    differ.currentList[position]?.let {
                        holder.bind(it)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonAbilityViewHolder -> {
                differ.currentList[position]?.let {
                    holder.bind(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<PokemonAbilityWithMetaData>) {
        differ.submitList(list)
    }

    private fun itemCallback(): DiffUtil.ItemCallback<PokemonAbilityWithMetaData> {
        return object : DiffUtil.ItemCallback<PokemonAbilityWithMetaData>() {

            override fun areItemsTheSame(
                oldItem: PokemonAbilityWithMetaData,
                newItem: PokemonAbilityWithMetaData
            ): Boolean {
                return oldItem.pokemonAbility.id == newItem.pokemonAbility.id
            }

            override fun areContentsTheSame(
                oldItem: PokemonAbilityWithMetaData,
                newItem: PokemonAbilityWithMetaData
            ): Boolean {
                return oldItem.pokemonAbility.id == newItem.pokemonAbility.id
                        && oldItem.pokemonAbility.name == newItem.pokemonAbility.name
            }

        }
    }

}

