package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail
import com.sealstudios.pokemonApp.databinding.EvolutionViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.EvolutionViewHolder

class EvolutionAdapter(private val glide: RequestManager) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = itemCallback()
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
                EvolutionViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EvolutionViewHolder(binding, glide)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EvolutionViewHolder -> {
                differ.currentList[position]?.let {
                    holder.bind(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<EvolutionDetail>) {
        differ.submitList(list)
    }

    private fun itemCallback(): DiffUtil.ItemCallback<EvolutionDetail> {
        return object : DiffUtil.ItemCallback<EvolutionDetail>() {

            override fun areItemsTheSame(
                    oldItem: EvolutionDetail,
                    newItem: EvolutionDetail
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                    oldItem: EvolutionDetail,
                    newItem: EvolutionDetail
            ): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.evolutionName == newItem.evolutionName
            }

        }
    }

}
