package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveTypeOrCategory
import com.sealstudios.pokemonApp.ui.animation.collapse
import com.sealstudios.pokemonApp.ui.animation.expand
import com.sealstudios.pokemonApp.ui.util.PokemonCategory
import com.sealstudios.pokemonApp.ui.util.PokemonCategory.Companion.getCategoryForDamageClass
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypeForPokemonType
import com.sealstudios.pokemonApp.ui.util.TypesAndCategoryGroupHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PokemonMoveViewHolder
constructor(
    private val binding: PokemonMoveViewHolderBinding,
    private val clickListener: PokemonMoveAdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonMove: PokemonMove, isExpanded: Boolean = false) = with(binding) {
        animateToggle(isExpanded)
        val generation = PokemonGeneration.getGeneration(pokemonMove.generation)
        //TODO change this to have the generation as a header to get the correct level learned at display on the tile

        Log.d(TAG,"move $pokemonMove")
        pokemonMoveNameTextView.text = pokemonMove.name.capitalize()
        levelLearnMethod.text = "Learnt by : ${pokemonMove.learnMethod.capitalize()}"
        levelLearnedAtTextView.text = "Learned at level : ${pokemonMove.levelLearnedAt}"

        generationText.text = PokemonGeneration.formatGenerationName(generation)
        powerText.text = pokemonMove.power.toString()
        accuracyText.text = "${pokemonMove.accuracy}%"
        ppText.text = pokemonMove.pp.toString()

        showMoreLessToggleButton.setOnClickListener {
            clickListener?.onItemSelected(bindingAdapterPosition, pokemonMove)
        }
        showMoreLessToggle.setOnClickListener {
            clickListener?.onItemSelected(bindingAdapterPosition, pokemonMove)
        }

        CoroutineScope(Dispatchers.Default).launch {
            val pokemonMoveTypeOrCategoryList = getPokemonMoveTypeOrCategoryList(pokemonMove)
            withContext(Dispatchers.Main){
                buildPokemonMoveTypeAndCategoryChips(pokemonMoveTypeOrCategoryList, binding)
                buildPokemonMoveTypeAndCategoryRibbons(pokemonMoveTypeOrCategoryList, binding)
            }
        }
    }

    private fun animateToggle(isExpanded: Boolean) {
        if (isExpanded) {
            rotateToggleOpen()
            binding.showMoreLessToggle.text = "Show less"
            expand(binding.expandedContent)
        } else {
            rotateToggleClose()
            binding.showMoreLessToggle.text = "Show more"
            collapse(binding.expandedContent)
        }
    }

    private fun rotateToggleOpen() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(180f)
    }

    private fun rotateToggleClose() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(0f)
    }

    private suspend fun getPokemonMoveTypeOrCategoryList(pokemonMove: PokemonMove): List<PokemonMoveTypeOrCategory> {
        return withContext(context = Dispatchers.Default){
            val typesOrCategoriesList = mutableListOf<PokemonMoveTypeOrCategory>()
            val type = getPokemonEnumTypeForPokemonType(pokemonMove.type)
            typesOrCategoriesList.add(
                PokemonMoveTypeOrCategory(
                    type = type,
                    category = null,
                    itemType = PokemonType.itemType
                )
            )
            val category = getCategoryForDamageClass(pokemonMove.damage_class)
            typesOrCategoriesList.add(
                PokemonMoveTypeOrCategory(
                    type = null,
                    category = category,
                    itemType = PokemonCategory.itemType
                )
            )
            return@withContext typesOrCategoriesList
        }
    }


    private fun buildPokemonMoveTypeAndCategoryChips(
        pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>,
        binding: PokemonMoveViewHolderBinding
    ) {
        TypesAndCategoryGroupHelper(
            binding.dualTypeChipLayout.pokemonTypesChipGroup,
            pokemonMoveTypeOrCategory
        ).bindChips()
    }

    private fun buildPokemonMoveTypeAndCategoryRibbons(
        pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>,
        binding: PokemonMoveViewHolderBinding
    ) {

        for (x in pokemonMoveTypeOrCategory.indices) {
            val pokemonTypeOrCategory = pokemonMoveTypeOrCategory[x]
            if (pokemonTypeOrCategory.itemType == PokemonType.itemType) {
                pokemonMoveTypeOrCategory[x].type?.let {
                    binding.typeRibbon.colorFilter = null;
                    binding.typeRibbon.setColorFilter(ContextCompat.getColor(binding.root.context, it.color), PorterDuff.Mode.SRC_IN);
                    binding.typeRibbon.visibility = View.VISIBLE
                }
            } else {
                pokemonMoveTypeOrCategory[x].category?.let {
                    binding.categoryRibbon.colorFilter = null;
                    binding.categoryRibbon.setColorFilter(ContextCompat.getColor(binding.root.context, it.color), PorterDuff.Mode.SRC_IN);
                    binding.categoryRibbon.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object {
        const val layoutType = 1000
        const val TAG = "MOVE_VIEW_HOLDER"
    }

}