package com.sealstudios.pokemonApp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonTypeHelper
import java.util.*

class PokemonViewHolder constructor(
    itemView: View,
    private val clickListener: ClickListener?,
    private val glide: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = PokemonViewHolderBinding.bind(itemView)

    @SuppressLint("DefaultLocale")
    fun bind(pokemon: Pokemon) = with(binding) {
        val pokemonTypes = PokemonTypeHelper().getPokemonTypesForPokemon(pokemon)

        binding.pokemonNameTextView.text = pokemon.name.capitalize()
        binding.pokemonSpeciesTextViewLabel.text = pokemon.species.capitalize()
        binding.pokemonImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.root.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, pokemon)
        }
        buildPokemonImageView(pokemon)
        binding.pokemonTypesChipGroup.removeAllViews()
        for (pokemonType in pokemonTypes) {
            binding.pokemonTypesChipGroup.addView(createChip(pokemonType, itemView.context))
        }
    }

    private fun buildPokemonImageView(pokemon: Pokemon) {
        glide.asBitmap()
            .load(pokemon.url)
            .placeholder(R.drawable.empty_pokemon)
            .listener(requestListener())
            .into(binding.pokemonImageView)
    }

    private fun requestListener(): RequestListener<Bitmap?> {
        return object : RequestListener<Bitmap?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Bitmap?>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                resource?.let { bitmap ->
                    val builder = Palette.Builder(bitmap)
                    builder.generate { palette: Palette? ->
                        palette?.dominantSwatch?.rgb?.let {
                            binding.pokemonImageView.apply {
                                borderColor = it
                            }
                        }
                        palette?.lightVibrantSwatch?.rgb?.let {
                            binding.pokemonImageView.apply {
                                circleColor = it
                            }
                        }
                    }
                }
                return false
            }
        }
    }

    private fun createChip(pokemonType: PokemonType, context: Context): Chip? {
        val chipLayout = LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip
        chipLayout.text = pokemonType.name.capitalize()
        chipLayout.chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
        chipLayout.setChipBackgroundColorResource(pokemonType.color)
        return chipLayout
    }

}



