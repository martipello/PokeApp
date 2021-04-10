package com.sealstudios.pokemonApp.ui.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.GenerationHeaderViewHolderBinding
import com.sealstudios.pokemonApp.databinding.MoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.MoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveAdapterItem
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.GenerationHeaderViewHolder
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.MoveViewHolder

class MoveAdapter(private val clickListener: MoveAdapterClickListener? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    private val diffCallback = itemCallback()
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MoveViewHolder.layoutType -> {
                val binding = MoveViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MoveViewHolder(binding, clickListener)
            }
            GenerationHeaderViewHolder.layoutType -> {
                val binding =
                        GenerationHeaderViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                is MoveViewHolder -> {
                    differ.currentList[position].moveWithMetaData?.let {
                        holder.bind(it, selectedItems.get(position, false))
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoveViewHolder -> {
                differ.currentList[position].moveWithMetaData?.let {
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

    fun submitList(list: List<MoveAdapterItem>) {
        differ.submitList(list)
    }

    private fun itemCallback(): DiffUtil.ItemCallback<MoveAdapterItem> {
        return object : DiffUtil.ItemCallback<MoveAdapterItem>() {

            override fun areItemsTheSame(oldItem: MoveAdapterItem, newItem: MoveAdapterItem): Boolean {
                return oldItem.moveWithMetaData?.move?.id == newItem.moveWithMetaData?.move?.id
            }

            override fun areContentsTheSame(oldItem: MoveAdapterItem, newItem: MoveAdapterItem): Boolean {
                return oldItem.moveWithMetaData?.move?.id == newItem.moveWithMetaData?.move?.id
                        && oldItem.moveWithMetaData?.move?.name == newItem.moveWithMetaData?.move?.name
            }

        }
    }

    companion object {
        const val expandSelected: Int = 100
    }
}

