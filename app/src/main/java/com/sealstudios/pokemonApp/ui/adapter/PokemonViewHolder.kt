package com.sealstudios.pokemonApp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonType.Companion.getTypesInOrder
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.createPokemonTypeChip

class PokemonViewHolder constructor(
    itemView: View,
    private val clickListener: PokemonAdapterClickListener?,
    private val glide: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = PokemonViewHolderBinding.bind(itemView)

    @SuppressLint("DefaultLocale")
    fun bind(pokemonWithTypesAndSpecies: PokemonWithTypesAndSpecies) = with(binding) {
        binding.pokemonNameTextView.text = pokemonWithTypesAndSpecies.pokemon.name.capitalize()
        binding.pokemonIdTextViewLabel.text =
            itemView.context.getString(R.string.pokemonId, pokemonWithTypesAndSpecies.pokemon.id)
        binding.pokemonSpeciesTextViewLabel.text =
            pokemonWithTypesAndSpecies.species.species.capitalize()
        binding.pokemonImageView.apply {
            transitionName = pokemonTransitionNameForId(pokemonWithTypesAndSpecies.pokemon.id, this.context)
            scaleType = ImageView.ScaleType.CENTER_CROP
            borderWidth = this.context.resources.getDimension(R.dimen.small_margin_4dp)
        }
        setPokemonImageView(pokemonWithTypesAndSpecies.pokemon.image)
        binding.root.setOnClickListener {
            clickListener?.onItemSelected(pokemonWithTypesAndSpecies.pokemon, binding.pokemonImageView)
        }
        binding.pokemonTypesChipGroup.removeAllViews()
        val types =
            PokemonType.getPokemonEnumTypesForPokemonTypes(
                getTypesInOrder(
                    pokemonWithTypesAndSpecies.types
                )
            )
        for (type in types) {
            binding.pokemonTypesChipGroup.addView(createPokemonTypeChip(type, itemView.context))
        }
    }

    private fun setPokemonImageView(pokemonImage: String) {
        glide.asBitmap()
            .load(pokemonImage)
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

    companion object {

        fun pokemonIdFromTransitionName(transitionName: String) = transitionName.split('_')[1]
        fun pokemonTransitionNameForId(id: Int, context: Context) = context.getString(R.string.transition_name, id)

    }

}



