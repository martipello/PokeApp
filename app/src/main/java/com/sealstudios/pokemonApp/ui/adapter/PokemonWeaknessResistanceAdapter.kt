package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.databinding.PokemonWeaknessResistanceViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonWeaknessResistanceViewHolder
import com.sealstudios.pokemonApp.ui.util.PokemonType

class PokemonWeaknessResistanceAdapter(
        private val glide: RequestManager
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = diffCallback()
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
                PokemonWeaknessResistanceViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonWeaknessResistanceViewHolder(
                binding,
                glide,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonWeaknessResistanceViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Pair<PokemonType, Double>>) {
        differ.submitList(list)
    }

    companion object {

        private fun diffCallback(): DiffUtil.ItemCallback<Pair<PokemonType, Double>> {
            return object : DiffUtil.ItemCallback<Pair<PokemonType, Double>>() {

                override fun areItemsTheSame(
                        oldItem: Pair<PokemonType, Double>,
                        newItem: Pair<PokemonType, Double>
                ): Boolean = oldItem.first == newItem.first

                override fun areContentsTheSame(
                        oldItem: Pair<PokemonType, Double>,
                        newItem: Pair<PokemonType, Double>
                ): Boolean = oldItem.first == newItem.first

            }
        }
    }
}
