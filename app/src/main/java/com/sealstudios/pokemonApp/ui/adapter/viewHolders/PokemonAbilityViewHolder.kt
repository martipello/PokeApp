package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonAbilityViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import java.util.*

class PokemonAbilityViewHolder
constructor(
    private val binding: PokemonAbilityViewHolderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(pokemonAbilityWithMetaData: PokemonAbilityWithMetaData) {
        binding.nameText.text = pokemonAbilityWithMetaData.pokemonAbility.name.capitalize(Locale.ROOT)
        binding.flavorTextText.text = pokemonAbilityWithMetaData.pokemonAbility.flavorText
        binding.generationText.text = PokemonGeneration.getGeneration(pokemonAbilityWithMetaData.pokemonAbility.generation).name
        binding.versionGroupText.text = pokemonAbilityWithMetaData.pokemonAbility.abilityEffectChangeVersionGroup
        binding.shortEffectText.text = pokemonAbilityWithMetaData.pokemonAbility.abilityEffectEntryShortEffect.replace("\\s".toRegex(), " ")
        binding.effectText.text = pokemonAbilityWithMetaData.pokemonAbility.abilityEffectEntry.replace("\\s".toRegex(), " ")
    }

    companion object {
        const val layoutType = 1002
    }

}
