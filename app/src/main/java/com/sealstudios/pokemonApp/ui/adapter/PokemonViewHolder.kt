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
        binding.pokemonImageViewHolder.apply {
            transitionName = pokemonTransitionNameForId(pokemonWithTypesAndSpecies.pokemon.id, this.context)
        }
        setPokemonImageView(pokemonWithTypesAndSpecies.pokemon.image)
        binding.root.setOnClickListener {
            clickListener?.onItemSelected(pokemonWithTypesAndSpecies.pokemon, binding.pokemonImageViewHolder)
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
                            binding.pokemonImageViewHolder.apply {
                                strokeColor = it
                            }
                        }
                        palette?.lightVibrantSwatch?.rgb?.let {
                            binding.pokemonImageViewHolder.apply {
                                setCardBackgroundColor(it)
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



