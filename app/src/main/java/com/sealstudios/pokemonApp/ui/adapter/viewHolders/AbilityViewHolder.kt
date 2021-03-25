package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Ability.Companion.formatAbilityName
import com.sealstudios.pokemonApp.database.`object`.wrappers.AbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.AbilityViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.AdapterClickListener
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.util.extensions.capitalize
import com.sealstudios.pokemonApp.util.extensions.removeWhiteSpace

class AbilityViewHolder
constructor(
        private val binding: AbilityViewHolderBinding,
        private val clickListener: AdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(abilityWithMetaData: AbilityWithMetaData, isExpanded: Boolean = false) {
        animateToggle(isExpanded)
        binding.nameText.text = formatAbilityName(abilityWithMetaData.ability.name)
        binding.hiddenAbility.text = binding.root.context.getString(
                R.string.hidden_ability,
                abilityWithMetaData.abilityMetaData.isHidden.toString()
        )
        binding.descriptionText.text = abilityWithMetaData.ability.flavorText
        binding.generationText.text =
                PokemonGeneration.formatGenerationName(PokemonGeneration.getGeneration(abilityWithMetaData.ability.generation))
        binding.versionText.text =
                abilityWithMetaData.ability.abilityEffectChangeVersionGroup.capitalize()
        binding.shortEffectText.text =
                abilityWithMetaData.ability.abilityEffectEntryShortEffect.removeWhiteSpace()
        binding.effectText.text =
                abilityWithMetaData.ability.abilityEffectEntry.removeWhiteSpace()
        binding.mainSeriesText.text = abilityWithMetaData.ability.isMainSeries.toString()
        binding.effectChangeText.text =
                if (abilityWithMetaData.ability.abilityEffectChange.isNotEmpty())
                    abilityWithMetaData.ability.abilityEffectChange
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
