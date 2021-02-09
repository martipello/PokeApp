package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.MoveLearningViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveLearning
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.MoveLearningViewHolder

class MoveLearningAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = diffCallback()
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
                MoveLearningViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoveLearningViewHolder(
                binding
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoveLearningViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<MoveLearning>) {
        differ.submitList(list)
    }

    companion object {

        private fun diffCallback(): DiffUtil.ItemCallback<MoveLearning> {
            return object : DiffUtil.ItemCallback<MoveLearning>() {

                override fun areItemsTheSame(oldItem: MoveLearning, newItem: MoveLearning): Boolean {
                    return oldItem.generation == newItem.generation
                }

                override fun areContentsTheSame(oldItem: MoveLearning, newItem: MoveLearning): Boolean {
                    return oldItem.generation == newItem.generation
                }

            }
        }

    }
}

