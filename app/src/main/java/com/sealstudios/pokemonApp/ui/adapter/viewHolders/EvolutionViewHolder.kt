package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.TextView
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
import com.sealstudios.pokemonApp.database.`object`.Gender
import com.sealstudios.pokemonApp.database.`object`.Gender.Companion.getGender
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.EvolutionViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PaletteHelper
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypeForPokemonType
import com.sealstudios.pokemonApp.ui.util.TypesAndCategoryGroupHelper
import com.sealstudios.pokemonApp.ui.util.TypesGroupHelper
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
        setEvolutionNameAndId(evolutionDetail)
        setPokemonImage(evolutionDetail)
        setTriggerView(evolutionDetail)
        setLevelView(evolutionDetail)
        setHappinessView(evolutionDetail)
        setBeautyView(evolutionDetail)
        setAffectionView(evolutionDetail)
        setLocationView(evolutionDetail)
        setKnownMoveType(evolutionDetail)
        setItem(evolutionDetail)
        setHeldItem(evolutionDetail)
        setGender(evolutionDetail)
        setTimeOfDay(evolutionDetail)
        setGender(evolutionDetail)
        setPartySpecies(evolutionDetail)
        setPartyType(evolutionDetail)
        setTradeSpecies(evolutionDetail)
    }

    private fun setEvolutionNameAndId(evolutionDetail: EvolutionDetail) {
        binding.pokemonNameTextView.text = evolutionDetail.evolutionName.capitalize()
        binding.pokemonIdTextViewLabel.text = itemView.context.getString(
                R.string.pokemonHashId,
                evolutionDetail.evolutionId
        )
    }

    private fun setPokemonImage(evolutionDetail: EvolutionDetail) {
        CoroutineScope(Dispatchers.Main).launch {
            setPokemonImageView(Pokemon.pokemonImage(evolutionDetail.evolutionId))
        }
    }

    private fun setTriggerView(evolutionDetail: EvolutionDetail) = with(binding) {
        val isNotNullOrNegative = evolutionDetail.triggerId.isNotNullOrNegative()
        if (isNotNullOrNegative) {
            val evolutionDetailTriggerText = EvolutionTrigger.getEvolutionTrigger(evolutionDetail.triggerId!!).name.capitalize()
            setTextViewForEvolutionDetailAttribute(evolutionDetailTriggerText, evolutionTriggerText, evolutionTriggerLabel)
        }
    }

    private fun setLevelView(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.minLevel, levelText, levelLabel)
    }

    private fun setHappinessView(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.minHappiness, happinessText, happinessLabel)
    }

    private fun setBeautyView(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.minBeauty, beautyText, beautyLabel)
    }

    private fun setAffectionView(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.minAffection, affectionText, affectionLabel)
    }

    private fun setLocationView(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.locationName, locationText, locationLabel)
    }

    private fun setKnownMoveType(evolutionDetail: EvolutionDetail) = with(binding) {
        val isNotNullOrEmpty = evolutionDetail.knownMoveType != null && evolutionDetail.knownMoveType.isNotEmpty()
        if (isNotNullOrEmpty) {
            val type = getPokemonEnumTypeForPokemonType(evolutionDetail.knownMoveType!!)
            setTextViewForEvolutionDetailAttribute(type.name, moveTypeText, moveTypeLabel)
        }
    }

    private fun setItem(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.itemName, itemText, itemLabel)
    }

    private fun setHeldItem(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.heldItemName, heldItemText, heldItemLabel)
    }

    private fun setTimeOfDay(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.timeOfDay, timeOfDayText, timeOfDayLabel)
    }

    private fun setGender(evolutionDetail: EvolutionDetail) = with(binding) {
        val isNotNullOrNegative = evolutionDetail.gender.isNotNullOrNegative()
        if (isNotNullOrNegative) {
            setTextViewForEvolutionDetailAttribute(getGender(evolutionDetail.gender!!).name, genderText, genderLabel)
        }
    }

    private fun setPartySpecies(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.partySpeciesName, partySpeciesText, partySpeciesLabel)
    }

    private fun setPartyType(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.partyTypeName, partyTypeText, partyTypeLabel)
    }

    private fun setTradeSpecies(evolutionDetail: EvolutionDetail) = with(binding) {
        setTextViewForEvolutionDetailAttribute(evolutionDetail.tradeSpeciesName, tradeSpeciesText, tradeSpeciesLabel)
    }

    private fun setTextViewForEvolutionDetailAttribute(evolutionDetailAttribute: String?, textView: TextView, textViewLabel: TextView) {
        val isNotNullOrEmpty = evolutionDetailAttribute != null && evolutionDetailAttribute.isNotEmpty()
        if (isNotNullOrEmpty) textView.text = evolutionDetailAttribute
        textView.visibility = if (isNotNullOrEmpty) View.VISIBLE else View.GONE
        textViewLabel.visibility = if (isNotNullOrEmpty) View.VISIBLE else View.GONE
    }

    private fun setTextViewForEvolutionDetailAttribute(evolutionDetailAttribute: Int?, textView: TextView, textViewLabel: TextView) {
        val isNotNullOrNegative = evolutionDetailAttribute.isNotNullOrNegative()
        if (isNotNullOrNegative) {
            textView.text = evolutionDetailAttribute.toString()
        }
        textView.visibility = if (isNotNullOrNegative) View.VISIBLE else View.GONE
        textViewLabel.visibility = if (isNotNullOrNegative) View.VISIBLE else View.GONE
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
