package com.sealstudios.pokemonApp.ui.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.database.`object`.wrappers.AbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.AbilityViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.AdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.AbilityViewHolder

class AbilityAdapter(private val clickListener: AdapterClickListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    private val diffCallback = itemCallback()
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
                AbilityViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbilityViewHolder(binding, clickListener)
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
                is AbilityViewHolder -> {
                    differ.currentList[position]?.let {
                        holder.bind(it, selectedItems.get(position, false))
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AbilityViewHolder -> {
                differ.currentList[position]?.let {
                    holder.bind(it, selectedItems.get(position, false))
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
        notifyItemChanged(pos, MoveAdapter.expandSelected)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<AbilityWithMetaData>) {
        differ.submitList(list)
    }

    private fun itemCallback(): DiffUtil.ItemCallback<AbilityWithMetaData> {
        return object : DiffUtil.ItemCallback<AbilityWithMetaData>() {

            override fun areItemsTheSame(
                    oldItem: AbilityWithMetaData,
                    newItem: AbilityWithMetaData
            ): Boolean {
                return oldItem.ability.id == newItem.ability.id
            }

            override fun areContentsTheSame(
                    oldItem: AbilityWithMetaData,
                    newItem: AbilityWithMetaData
            ): Boolean {
                return oldItem.ability.id == newItem.ability.id
                        && oldItem.ability.name == newItem.ability.name
            }

        }
    }

}

