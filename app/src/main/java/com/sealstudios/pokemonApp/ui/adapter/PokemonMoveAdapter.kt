package com.sealstudios.pokemonApp.ui.adapter

import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.GenerationHeaderViewHolderBinding
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveAdapterItem
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.GenerationHeaderViewHolder
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonMoveViewHolder

class PokemonMoveAdapter(private val clickListener: PokemonMoveAdapterClickListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    private val diffCallback = itemCallback()
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            PokemonMoveViewHolder.layoutType -> {
                val binding = PokemonMoveViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PokemonMoveViewHolder(binding, clickListener)
            }
            GenerationHeaderViewHolder.layoutType -> {
                val binding = GenerationHeaderViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GenerationHeaderViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
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
                is PokemonMoveViewHolder -> {
                    differ.currentList[position].move?.let {
                        holder.bind(it, selectedItems.get(position, false))
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonMoveViewHolder -> {
                differ.currentList[position].move?.let {
                    holder.bind(it, selectedItems.get(position, false))
                }
            }
            is GenerationHeaderViewHolder -> {
                differ.currentList[position].header?.let {
                    holder.bind(it)
                }
            }
        }
    }

    fun selectItem(pos: Int) {
        if (selectedItems[pos, false]) {
            selectedItems.put(pos, false)
        } else {
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos, expandSelected)
    }


    override fun getItemViewType(position: Int): Int {
        return differ.currentList[position].itemType
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<PokemonMoveAdapterItem>) {
        differ.submitList(list)
    }

    private fun itemCallback(): DiffUtil.ItemCallback<PokemonMoveAdapterItem> {
        return object : DiffUtil.ItemCallback<PokemonMoveAdapterItem>() {

            override fun areItemsTheSame(oldItem: PokemonMoveAdapterItem, newItem: PokemonMoveAdapterItem): Boolean {
                return oldItem.move?.id == newItem.move?.id
            }

            override fun areContentsTheSame(oldItem: PokemonMoveAdapterItem, newItem: PokemonMoveAdapterItem): Boolean {
                return oldItem.move?.id == newItem.move?.id && oldItem.move?.name == newItem.move?.name
            }

        }
    }

    companion object {
        const val expandSelected : Int = 100
    }
}

