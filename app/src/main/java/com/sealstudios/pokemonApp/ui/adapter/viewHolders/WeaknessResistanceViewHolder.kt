package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.WeaknessResistanceViewHolderBinding
import com.sealstudios.pokemonApp.ui.util.PokemonType
import java.text.DecimalFormat

val <A, B> Pair<A, B>.pokemonType: A get() = this.first
val <A, B> Pair<A, B>.weaknessResistanceMultiplier: B get() = this.second

class PokemonWeaknessResistanceViewHolder constructor(
        private val binding: WeaknessResistanceViewHolderBinding,
        private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonTypesWithWeaknessResistance: Pair<PokemonType, Double>) = with(binding) {
        glide.load(pokemonTypesWithWeaknessResistance.pokemonType.icon).into(weaknessResistanceType)
        val weaknessResistanceValue = getWeaknessResistanceValue(pokemonTypesWithWeaknessResistance.weaknessResistanceMultiplier)
        weaknessResistanceMultiplier.text = this.root.context.getString(
                R.string.multiplier,
                weaknessResistanceValue
        )
    }

    private fun getWeaknessResistanceValue(pokemonWeaknessResistance: Double): String {
        val decimalFormat = DecimalFormat("###.##")
        return when (val weaknessResistanceValue = decimalFormat.format(pokemonWeaknessResistance)) {
            "0.5" -> "1/2"
            "0.25" -> "1/4"
            else -> weaknessResistanceValue
        }
    }

}



