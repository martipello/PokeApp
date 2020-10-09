package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.databinding.PokemonTypeChipBinding
import com.sealstudios.pokemonApp.databinding.PokemonViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypeForPokemonType
import com.sealstudios.pokemonApp.ui.util.TypesGroupHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonMoveViewHolder
constructor(
    private val binding: PokemonMoveViewHolderBinding,
    private val clickListener: PokemonMoveAdapterClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonMove: PokemonMove) = with(binding) {
//        this.root.setOnClickListener {
//            clickListener?.onItemSelected(adapterPosition, pokemonMove)
//        }
        //TODO change this to have the generation as a header to get the correct level learned at display on the tile
        pokemonMoveNameTextView.text = pokemonMove.name
//        levelLearnedAtTextView.text = pokemonMove.
        ppText.text = pokemonMove.pp.toString()
        generationText.text = pokemonMove.generation
        powerText.text = pokemonMove.power.toString()
//        accuracyText.text = root.context.getString(R.string.accuracy_percentage, pokemonMove.accuracy)
        showMoreLessToggleButton.setOnClickListener {
            // animate
        }
        showMoreLessToggle.setOnClickListener {
            // animate
        }
        buildPokemonTypes(pokemonMove, binding)
    }

    private fun buildPokemonTypes(pokemonMove: PokemonMove, binding: PokemonMoveViewHolderBinding) {
        val types = mutableListOf<PokemonType>()
        //types .add type
        val type = getPokemonEnumTypeForPokemonType(pokemonMove.type)
        types.add(type)
        TypesGroupHelper(binding.dualTypeChipLayout.pokemonTypesChipGroup, types).bindChips()
        //types .add category

    }

}