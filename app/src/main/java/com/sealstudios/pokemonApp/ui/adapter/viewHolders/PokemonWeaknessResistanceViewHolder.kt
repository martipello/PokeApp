package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypesAndSpeciesForList
import com.sealstudios.pokemonApp.databinding.PokemonWeaknessResistanceViewHolderBinding

val <A, B> Pair<A, B>.pokemonType: A get() = this.first
val <A, B> Pair<A, B>.weaknessResistance: B get() = this.second

class PokemonWeaknessResistanceViewHolder constructor(
    private val binding: PokemonWeaknessResistanceViewHolderBinding,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("DefaultLocale")
    fun bind(pokemonTypesWithWeaknessResistance: Pair<String, String>) = with(binding) {
        pokemonTypesWithWeaknessResistance.pokemonType
        pokemonTypesWithWeaknessResistance.weaknessResistance
    }

}



