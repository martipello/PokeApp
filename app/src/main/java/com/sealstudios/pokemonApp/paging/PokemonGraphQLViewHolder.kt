package com.sealstudios.pokemonApp.paging

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PaletteHelper
import com.sealstudios.pokemonApp.ui.util.TypesGroupHelper
import com.sealstudios.pokemonApp.util.extensions.capitalize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sealstudios.pokemonApp.database.`object`.Type as dbType
import com.sealstudios.pokemonApp.ui.util.PokemonType as networkType

class PokemonGraphQLViewHolder constructor(
        private val binding: PokemonViewHolderBinding,
        private val clickListener: PokemonPagingAdapterClickListener?,
        private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("DefaultLocale")
    fun bind(pokemon: PokemonGraphQL) = with(binding) {
        setViewHolderDefaultState()
        CoroutineScope(Dispatchers.Main).launch {
            setPokemonImageView(Pokemon.pokemonImage(pokemon.id))
        }

        binding.pokemonNameTextView.text = pokemon.name.capitalize()
        binding.pokemonIdTextViewLabel.text =
                itemView.context.getString(
                        R.string.pokemonHashId,
                        pokemon.id
                )
        binding.pokemonSpeciesTextViewLabel.text =
                pokemon.speciesName.capitalize()
        binding.pokemonImageViewHolder.apply {
            transitionName =
                    pokemonTransitionNameForId(
                            pokemon.id,
                            this.context
                    )
        }
        binding.root.setOnClickListener {
            clickListener?.onItemSelected(
                    pokemon,
                    binding.pokemonImageViewHolder
            )
        }
//        buildPokemonTypes(pokemon.types.map { dbType }, binding)
    }

    private fun setViewHolderDefaultState() {
        val primaryLightColor: Int = ContextCompat.getColor(binding.root.context, R.color.primaryLightColor)

        binding.pokemonImageViewHolder.strokeColor = primaryLightColor
        binding.pokemonImageViewHolder.setCardBackgroundColor(primaryLightColor)
        binding.dualTypeChipLayout.typeChip1.pokemonTypeChip.visibility = View.GONE
        binding.dualTypeChipLayout.typeChip2.pokemonTypeChip.visibility = View.GONE
    }

    private fun buildPokemonTypes(
            types: List<dbType>,
            binding: PokemonViewHolderBinding
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val enumTypes = networkType.getPokemonEnumTypesForPokemonTypes(
                    types
            )
            withContext(Dispatchers.Main) {
                TypesGroupHelper(
                        binding.dualTypeChipLayout.pokemonTypesChipGroup,
                        enumTypes
                ).bindChips()
            }
        }
    }

    private fun setPokemonImageView(pokemonImage: String) {
        val requestOptions =
                RequestOptions.placeholderOf(R.drawable.pokeball_vector).dontTransform()
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
                    setBackgroundAndStrokeColorFromPaletteForBitmap(bitmap, binding.root.context)
                }
                return false
            }
        }
    }

    private fun setBackgroundAndStrokeColorFromPaletteForBitmap(bitmap: Bitmap, context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            PaletteHelper.setLightAndDarkVibrantColorForBitmap(bitmap, context, ::setColoredElements)
        }
    }

    private suspend fun setColoredElements(lightVibrantColor: Int, darkVibrantColor: Int) {
        withContext(Dispatchers.Default){
            binding.pokemonImageViewHolder.strokeColor = lightVibrantColor
            binding.pokemonImageViewHolder.setCardBackgroundColor(darkVibrantColor)
        }
    }

    companion object {
        fun pokemonIdFromTransitionName(transitionName: String) = transitionName.split('_')[1]
        fun pokemonTransitionNameForId(id: Int, context: Context) =
                context.getString(R.string.transition_name, id)
    }
}



