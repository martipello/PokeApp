package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility.Companion.formatAbilityName
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonAbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonAbilityViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.AdapterClickListener
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.util.extensions.capitalize
import com.sealstudios.pokemonApp.util.extensions.removeWhiteSpace

class PokemonAbilityViewHolder
constructor(
        private val binding: PokemonAbilityViewHolderBinding,
        private val clickListener: AdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonAbilityWithMetaData: PokemonAbilityWithMetaData, isExpanded: Boolean = false) {
        animateToggle(isExpanded)
        binding.nameText.text = formatAbilityName(pokemonAbilityWithMetaData.pokemonAbility.name)
        binding.hiddenAbility.text = binding.root.context.getString(
                R.string.hidden_ability,
                pokemonAbilityWithMetaData.pokemonAbilityMetaData.isHidden.toString()
        )
        binding.descriptionText.text = pokemonAbilityWithMetaData.pokemonAbility.flavorText
        binding.generationText.text =
                PokemonGeneration.formatGenerationName(PokemonGeneration.getGeneration(pokemonAbilityWithMetaData.pokemonAbility.generation))
        binding.versionText.text =
                pokemonAbilityWithMetaData.pokemonAbility.abilityEffectChangeVersionGroup.capitalize()
        binding.shortEffectText.text =
                pokemonAbilityWithMetaData.pokemonAbility.abilityEffectEntryShortEffect.removeWhiteSpace()
        binding.effectText.text =
                pokemonAbilityWithMetaData.pokemonAbility.abilityEffectEntry.removeWhiteSpace()
        binding.mainSeriesText.text = pokemonAbilityWithMetaData.pokemonAbility.isMainSeries.toString()
        binding.effectChangeText.text =
                if (pokemonAbilityWithMetaData.pokemonAbility.abilityEffectChange.isNotEmpty())
                    pokemonAbilityWithMetaData.pokemonAbility.abilityEffectChange
                else "No effect change"

        binding.showMoreLessToggle.setOnClickListener {
            clickListener?.onItemSelected(bindingAdapterPosition)
        }
        binding.showMoreLessToggleButton.setOnClickListener {
            clickListener?.onItemSelected(bindingAdapterPosition)
        }

    }

    private fun animateToggle(isExpanded: Boolean) {
        if (isExpanded) {
            rotateToggleOpen()
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_less)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_less)
            binding.motionLayout.transitionToEnd()
        } else {
            rotateToggleClose()
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_more)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_more)
            binding.motionLayout.transitionToStart()
        }
    }

    private fun rotateToggleOpen() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(180f)
    }

    private fun rotateToggleClose() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(0f)
    }

    companion object

}
