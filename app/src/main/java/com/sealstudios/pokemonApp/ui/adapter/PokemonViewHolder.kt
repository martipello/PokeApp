package com.sealstudios.pokemonApp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
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
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.getTypesInOrder
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PokemonType

class PokemonViewHolder constructor(
    itemView: View,
    private val clickListener: ClickListener?,
    private val glide: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = PokemonViewHolderBinding.bind(itemView)

    @SuppressLint("DefaultLocale")
    fun bind(pokemonWithTypes: PokemonWithTypes) = with(binding) {
        Log.d("VH", pokemonWithTypes.toString())
        binding.pokemonNameTextView.text = pokemonWithTypes.pokemon.name.capitalize()
        binding.pokemonIdTextViewLabel.text = itemView.context.getString(R.string.pokemonId, pokemonWithTypes.pokemon.id)
        binding.pokemonSpeciesTextViewLabel.text = pokemonWithTypes.pokemon.species.capitalize()
        binding.pokemonImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.pokemonImageView.shadowColor = R.color.black
        binding.root.setOnClickListener {
            clickListener?.onItemSelected(adapterPosition, pokemonWithTypes.pokemon)
        }
        buildPokemonImageView(pokemonWithTypes.pokemon)
        binding.pokemonTypesChipGroup.removeAllViews()
        val types = PokemonType.getPokemonEnumTypesForPokemonTypes(getTypesInOrder(pokemonWithTypes.types))
        for (type in types) {
            binding.pokemonTypesChipGroup.addView(createChip(type, itemView.context))
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

    @SuppressLint("DefaultLocale")
    private fun createChip(pokemonType: PokemonType, context: Context): Chip? {
        val chipLayout =
            LayoutInflater.from(context).inflate(R.layout.pokemon_type_chip, null) as Chip
        chipLayout.text = pokemonType.name.capitalize()
        chipLayout.chipIcon = ContextCompat.getDrawable(context, pokemonType.icon)
        chipLayout.setChipBackgroundColorResource(pokemonType.color)
        return chipLayout
    }

}



