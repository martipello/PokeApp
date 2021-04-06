package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.EvolutionDetail
import com.sealstudios.pokemonApp.database.`object`.EvolutionTrigger
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.EvolutionViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PaletteHelper
import com.sealstudios.pokemonApp.util.extensions.capitalize
import com.sealstudios.pokemonApp.util.extensions.isNotNullOrNegative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EvolutionViewHolder
constructor(
        private val binding: EvolutionViewHolderBinding,
        private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(evolutionDetail: EvolutionDetail) {
        handleEvolutionNameAndId(evolutionDetail)
        handlePokemonImage(evolutionDetail)
        handleTriggerView(evolutionDetail)
        handleLevelView(evolutionDetail)
        handleHappinessView(evolutionDetail)
        handleBeautyView(evolutionDetail)
        handleAffectionView(evolutionDetail)
    }

    private fun handleEvolutionNameAndId(evolutionDetail: EvolutionDetail) {
        binding.pokemonNameTextView.text = evolutionDetail.evolutionName.capitalize()
        binding.pokemonIdTextViewLabel.text = itemView.context.getString(
                R.string.pokemonHashId,
                evolutionDetail.evolutionId
        )
    }

    private fun handlePokemonImage(evolutionDetail: EvolutionDetail) {
        CoroutineScope(Dispatchers.Main).launch {
            setPokemonImageView(Pokemon.pokemonImage(evolutionDetail.evolutionId))
        }
    }

    private fun handleAffectionView(evolutionDetail: EvolutionDetail) {
        if (evolutionDetail.minAffection.isNotNullOrNegative()) {
            binding.affectionText.text = evolutionDetail.minAffection.toString()
        } else {
            binding.affectionLabel.visibility = View.GONE
        }
    }

    private fun handleBeautyView(evolutionDetail: EvolutionDetail) {
        if (evolutionDetail.minBeauty.isNotNullOrNegative()) {
            binding.beautyText.text = evolutionDetail.minBeauty.toString()
        } else {
            binding.beautyLabel.visibility = View.GONE
        }
    }

    private fun handleHappinessView(evolutionDetail: EvolutionDetail) {
        if (evolutionDetail.minHappiness.isNotNullOrNegative()) {
            binding.happinessText.text = evolutionDetail.minHappiness.toString()
        } else {
            binding.happinessLabel.visibility = View.GONE
        }
    }

    private fun handleLevelView(evolutionDetail: EvolutionDetail) {
        if (evolutionDetail.minLevel.isNotNullOrNegative()) {
            binding.levelText.text = evolutionDetail.minLevel.toString()
        } else {
            binding.levelLabel.visibility = View.GONE
        }
    }

    private fun handleTriggerView(evolutionDetail: EvolutionDetail) {
        if (evolutionDetail.triggerId.isNotNullOrNegative()) {
            binding.evolutionTriggerText.text = EvolutionTrigger.getEvolutionTrigger(evolutionDetail.triggerId!!).name.capitalize()
        } else {
            binding.evolutionTriggerLabel.visibility = View.GONE
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

    private fun setColoredElements(lightVibrantColor: Int, darkVibrantColor: Int) {
        binding.pokemonImageViewHolder.strokeColor = lightVibrantColor
        binding.pokemonImageViewHolder.setCardBackgroundColor(darkVibrantColor)
    }

}
