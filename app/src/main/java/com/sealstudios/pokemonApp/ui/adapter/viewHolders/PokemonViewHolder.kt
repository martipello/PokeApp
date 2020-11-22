package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.TypesGroupHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonViewHolder constructor(
    private val binding: PokemonViewHolderBinding,
    private val clickListener: PokemonAdapterClickListener?,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("DefaultLocale")
    fun bind(pokemonWithTypesAndSpecies: PokemonWithTypesAndSpecies) = with(binding) {
        val white: Int = ContextCompat.getColor(binding.root.context, R.color.white)

        if (pokemonWithTypesAndSpecies.species == null) {
            CoroutineScope(Dispatchers.IO).launch {
                fetchRemotePokemon(pokemonWithTypesAndSpecies.pokemon.id)
            }
        }
        binding.pokemonImageViewHolder.strokeColor = white
        binding.pokemonImageViewHolder.setCardBackgroundColor(white)
        setPokemonImageView(pokemonWithTypesAndSpecies.pokemon.image)
        binding.dualTypeChipLayout.typeChip1.pokemonTypeChip.visibility = View.INVISIBLE
        binding.dualTypeChipLayout.typeChip2.pokemonTypeChip.visibility = View.GONE
        binding.pokemonNameTextView.text = pokemonWithTypesAndSpecies.pokemon.name.capitalize()
        binding.pokemonIdTextViewLabel.text =
            itemView.context.getString(R.string.pokemonHashId, pokemonWithTypesAndSpecies.pokemon.id)
        binding.pokemonSpeciesTextViewLabel.text =
            pokemonWithTypesAndSpecies.species?.species?.capitalize()
        binding.pokemonImageViewHolder.apply {
            transitionName =
                pokemonTransitionNameForId(
                    pokemonWithTypesAndSpecies.pokemon.id,
                    this.context
                )
        }
        binding.root.setOnClickListener {
            clickListener?.onItemSelected(
                pokemonWithTypesAndSpecies.pokemon,
                binding.pokemonImageViewHolder
            )
        }
        buildPokemonTypes(pokemonWithTypesAndSpecies, binding)
    }

    private suspend fun fetchRemotePokemon(id: Int) = withContext(Dispatchers.IO) {
//        remoteRepositoryHelper.fetchPokemonForId(id)
//        remoteRepositoryHelper.fetchSpeciesForId(id)
    }

    private fun buildPokemonTypes(pokemon: PokemonWithTypesAndSpecies, binding: PokemonViewHolderBinding) {
        CoroutineScope(Dispatchers.Default).launch {
            val types = PokemonType.getPokemonEnumTypesForPokemonTypes(
                    pokemon.types
            )
            withContext(Dispatchers.Main){
                TypesGroupHelper(binding.dualTypeChipLayout.pokemonTypesChipGroup, types).bindChips()
            }
        }
    }

    private fun setPokemonImageView(pokemonImage: String) {
        val requestOptions = RequestOptions.placeholderOf(R.drawable.pokeball_vector).dontTransform()
        glide.asBitmap()
            .load(pokemonImage)
            .apply(requestOptions)
            .format(DecodeFormat.PREFER_RGB_565)
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
                    setBackgroundAndStrokeColorFromPaletteForBitmap(bitmap)
                }
                return false
            }
        }
    }

    private fun setBackgroundAndStrokeColorFromPaletteForBitmap(bitmap: Bitmap) {
        val white: Int = ContextCompat.getColor(binding.root.context, R.color.white)
        CoroutineScope(Dispatchers.Default).launch {
            Palette.Builder(bitmap).generate {
                val lightVibrantColor =
                    it?.lightVibrantSwatch?.rgb ?: it?.dominantSwatch?.rgb ?: white
                val darkVibrantColor =
                    it?.darkVibrantSwatch?.rgb ?: it?.dominantSwatch?.rgb ?: white

                binding.pokemonImageViewHolder.strokeColor = lightVibrantColor
                binding.pokemonImageViewHolder.setCardBackgroundColor(darkVibrantColor)
            }
        }
    }

    companion object {

        fun pokemonIdFromTransitionName(transitionName: String) = transitionName.split('_')[1]
        fun pokemonTransitionNameForId(id: Int, context: Context) =
            context.getString(R.string.transition_name, id)
    }

}



