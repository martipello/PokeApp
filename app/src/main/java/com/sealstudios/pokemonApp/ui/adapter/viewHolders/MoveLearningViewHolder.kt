package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.MoveLearningViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveLearning
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration.Companion.formatGenName
import java.util.*

class MoveLearningViewHolder constructor(
    private val binding: MoveLearningViewHolderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(moveLearning: MoveLearning) = with(binding) {
        binding.genCell.text = formatGenName(moveLearning.generation)
        binding.learnMethodCell.text = moveLearning.learntBy.capitalize(Locale.ROOT)
        binding.learnedAtCell.text = "${moveLearning.learntAt}"
    }

}