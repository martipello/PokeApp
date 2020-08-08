package com.sealstudios.pokemonApp.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.loadCircularImage

class PokemonViewHolder constructor(
    itemView: View,
    private val clickListener: ClickListener?,
    private val requestManager: RequestManager?
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = PokemonViewHolderBinding.bind(itemView)

    @SuppressLint("DefaultLocale")
    fun bind(pokemon: Pokemon) = with(binding) {
        binding.pokemonNameTextView.text = pokemon.name.capitalize()
        itemView.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, pokemon)
        }
        binding.pokemonImageView.loadCircularImage(
            model = pokemon.url,
            borderSize = 2.0f,
            borderColor = Color.BLUE,
            glide = requestManager,
            listener = ::requestListener
        )
    }

    private fun requestListener(): RequestListener<Bitmap?> {
        return object : RequestListener<Bitmap?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Bitmap?>,
                isFirstResource: Boolean
            ): Boolean {
                binding.pokemonImageProgressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                binding.pokemonImageProgressBar.visibility = View.GONE
                return false
            }
        }
    }
}



