package com.sealstudios.pokemonApp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonMoveViewHolder

class PokemonMoveAdapter(private val clickListener: PokemonMoveAdapterClickListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = itemCallback()
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PokemonMoveViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonMoveViewHolder(
            binding,
            clickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonMoveViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<PokemonMove>) {
        differ.submitList(list)
    }


    private fun itemCallback(): DiffUtil.ItemCallback<PokemonMove> {
        return object : DiffUtil.ItemCallback<PokemonMove>() {

            override fun areItemsTheSame(oldItem: PokemonMove, newItem: PokemonMove): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PokemonMove, newItem: PokemonMove): Boolean {
                return oldItem.id == newItem.id && oldItem.name == newItem.name
            }

        }
    }
}

