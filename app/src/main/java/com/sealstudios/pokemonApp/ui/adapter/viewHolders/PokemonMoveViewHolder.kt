package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypeForPokemonType
import com.sealstudios.pokemonApp.ui.util.TypesGroupHelper


class PokemonMoveViewHolder
constructor(
    private val binding: PokemonMoveViewHolderBinding,
    private val clickListener: PokemonMoveAdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonMove: PokemonMove, isExpanded: Boolean = false) = with(binding) {
        animateToggle(isExpanded)
        //TODO change this to have the generation as a header to get the correct level learned at display on the tile
        pokemonMoveNameTextView.text = pokemonMove.name.capitalize()
        levelLearnedAtTextView.text = "Learned at level : ${pokemonMove.levelsLearnedAt.first()}"
        ppText.text = pokemonMove.pp.toString()
        generationText.text = pokemonMove.generation
        powerText.text = if (pokemonMove.power.toString().isNotEmpty()) pokemonMove.power.toString() else "n/a"
//        accuracyText.text = root.context.getString(R.string.accuracy_percentage, pokemonMove.accuracy)
        showMoreLessToggleButton.setOnClickListener {
//            animateToggle(isExpanded)
            clickListener?.onItemSelected(adapterPosition, pokemonMove)
        }
        showMoreLessToggle.setOnClickListener {
//            animateToggle(isExpanded)
            clickListener?.onItemSelected(adapterPosition, pokemonMove)
        }
        buildPokemonTypes(pokemonMove, binding)
    }

    private fun animateToggle(isExpanded: Boolean) {
        if (isExpanded) {
            rotateToggleOpen()
            binding.showMoreLessToggle.text = "Show less"
            binding.expandedContent.visibility = View.VISIBLE
        } else {
            rotateToggleClose()
            binding.showMoreLessToggle.text = "Show more"
            binding.expandedContent.visibility = View.GONE
        }
    }

    private fun rotateToggleOpen(){
        val rotateAnimation = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        binding.showMoreLessToggleButton.startAnimation(rotateAnimation)
    }

    private fun rotateToggleClose(){
        val rotateAnimation = RotateAnimation(
            1800f, 0f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        binding.showMoreLessToggleButton.startAnimation(rotateAnimation)
    }

    private fun buildPokemonTypes(pokemonMove: PokemonMove, binding: PokemonMoveViewHolderBinding) {
        val types = mutableListOf<PokemonType>()
        //types .add type
        val type = getPokemonEnumTypeForPokemonType(pokemonMove.type)
        types.add(type)
        TypesGroupHelper(binding.dualTypeChipLayout.pokemonTypesChipGroup, types).bindChips()
        //types .add category

    }

    companion object  {
        const val layoutType = 1000
    }

}