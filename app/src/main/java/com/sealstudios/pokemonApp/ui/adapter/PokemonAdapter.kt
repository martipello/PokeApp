package com.sealstudios.pokemonApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import javax.inject.Inject

class PokemonAdapter(private val clickListener: ClickListener? = null, private val glide: RequestManager? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = diffCallback()

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PokemonViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.pokemon_view_holder,
                        parent,
                        false
                ),
                clickListener,
                glide
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

    fun submitList(list: List<Pokemon>) {
        differ.submitList(list)
    }


    companion object {
        private fun diffCallback(): DiffUtil.ItemCallback<Pokemon> {
            return object : DiffUtil.ItemCallback<Pokemon>() {

                override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                    return oldItem.id == newItem.id // TODO add other items to this
                }

            }
        }
    }


}

