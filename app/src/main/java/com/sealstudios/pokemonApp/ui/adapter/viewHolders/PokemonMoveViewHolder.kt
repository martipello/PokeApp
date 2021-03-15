package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonMove.Companion.typeOrCategoryList
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveMetaData.Companion.moveLearningList
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonMoveWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.MoveLearningAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveTypeOrCategory
import com.sealstudios.pokemonApp.ui.extensions.removeItemDecorations
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitTransitionEnd
import com.sealstudios.pokemonApp.ui.util.PokemonCategory
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.TypesAndCategoryGroupHelper
import com.sealstudios.pokemonApp.ui.util.decorators.JustBottomDecoration
import com.sealstudios.pokemonApp.util.extensions.capitalize
import com.sealstudios.pokemonApp.util.extensions.names
import com.sealstudios.pokemonApp.util.extensions.removeWhiteSpace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonMoveViewHolder
constructor(
        private val binding: PokemonMoveViewHolderBinding,
        private val clickListener: PokemonMoveAdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var learningAdapter: MoveLearningAdapter

    fun bind(pokemonMoveWithMetaData: PokemonMoveWithMetaData, isExpanded: Boolean = false) = with(binding) {

        if (isExpanded) {
            binding.setExpanded(pokemonMoveWithMetaData)
        } else {
            binding.setNotExpanded()
        }

        populateTextViews(pokemonMoveWithMetaData)
        setChipsAndRibbons(pokemonMoveWithMetaData)

        showMoreLessToggle.setOnClickListener {
            onExpandCollapse(isExpanded, pokemonMoveWithMetaData)
        }

        showMoreLessToggleButton.setOnClickListener {
            onExpandCollapse(isExpanded, pokemonMoveWithMetaData)
        }
    }

    private fun onExpandCollapse(isExpanded: Boolean, pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.Main) {
                if (!isExpanded) {
                    showLearningTable(pokemonMoveWithMetaData)
                }
                animateToggle(!isExpanded)
                animateExpandedContent(isExpanded)
            }
            binding.moveViewHolderRoot.awaitTransitionEnd()
            withContext(Dispatchers.Main) {
                clickListener?.onItemSelected(bindingAdapterPosition, pokemonMoveWithMetaData.pokemonMove)
            }
        }
    }

    private fun animateToggle(isExpanded: Boolean) {
        if (isExpanded) {
            rotateToggleOpen()
            binding.setShowMoreLessTextToLess()
        } else {
            rotateToggleClose()
            binding.setShowMoreLessTextToMore()
        }
    }

    private fun animateExpandedContent(isExpanded: Boolean) {
        if (isExpanded) {
            binding.moveViewHolderRoot.transitionToStart()
        } else {
            binding.moveViewHolderRoot.transitionToEnd()
        }
    }

    private fun showLearningTable(pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        setUpMoveLearningAdapter(pokemonMoveWithMetaData)
        setUpLearningRecyclerView()
    }

    private fun setUpMoveLearningAdapter(pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        learningAdapter = MoveLearningAdapter()
        CoroutineScope(Dispatchers.Default).launch {
            val moveLearningList = pokemonMoveWithMetaData.pokemonMoveMetaData.moveLearningList()

            if (moveLearningList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    binding.showTableAndHeader()
                }
            }
            learningAdapter.submitList(moveLearningList)
        }
    }

    private fun setUpLearningRecyclerView() {
        binding.levelLearnedAtTable.apply {
            adapter = learningAdapter
            removeItemDecorations()
            addItemDecoration(
                    JustBottomDecoration(
                            context.resources.getDimensionPixelSize(
                                    R.dimen.qualified_small_margin_8dp
                            )
                    )
            )
            suppressLayout(true)
        }
    }

    private fun rotateToggleOpen() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(180f)
    }

    private fun rotateToggleClose() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(0f)
    }

    private suspend fun buildPokemonMoveTypeAndCategoryChips(
            pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>
    ) {
        withContext(Dispatchers.Default) {
            TypesAndCategoryGroupHelper(
                    binding.dualTypeChipLayout.pokemonTypesChipGroup,
                    pokemonMoveTypeOrCategory
            ).bindChips()
        }
    }

    private suspend fun buildPokemonMoveTypeAndCategoryRibbons(
            pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>
    ) {
        withContext(Dispatchers.Default) {
            for (index in pokemonMoveTypeOrCategory.indices) {
                val pokemonTypeOrCategory = pokemonMoveTypeOrCategory[index]
                if (pokemonTypeOrCategory.itemType == PokemonType.itemType) {
                    pokemonMoveTypeOrCategory[index].type?.let {
                        setPokemonMoveTypeRibbon(it)
                    }
                } else {
                    pokemonMoveTypeOrCategory[index].category?.let {
                        setPokemonMoveCategoryRibbon(it)
                    }
                }
            }
        }
    }

    private suspend fun setPokemonMoveCategoryRibbon(
            pokemonCategory: PokemonCategory
    ) {
        withContext(Dispatchers.Main) {
            binding.categoryRibbon.colorFilter = null
            binding.categoryRibbon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, pokemonCategory.color),
                    PorterDuff.Mode.SRC_IN
            )
            binding.categoryRibbon.visibility = View.VISIBLE
        }
    }

    private suspend fun setPokemonMoveTypeRibbon(
            pokemonType: PokemonType
    ) {
        withContext(Dispatchers.Main) {
            binding.typeRibbon.colorFilter = null
            binding.typeRibbon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, pokemonType.color),
                    PorterDuff.Mode.SRC_IN
            )
            binding.typeRibbon.visibility = View.VISIBLE
        }
    }

    companion object {
        const val layoutType = 1000
    }

    @SuppressLint("StringFormatInvalid")
    private fun PokemonMoveViewHolderBinding.populateTextViews(
            pokemonMoveWithMetaData: PokemonMoveWithMetaData
    ) {
        val generation = PokemonGeneration.getGeneration(pokemonMoveWithMetaData.pokemonMove.generation)
        val description = pokemonMoveWithMetaData.pokemonMove.description.removeWhiteSpace()

        descriptionText.text = description
        pokemonMoveNameTextView.text = pokemonMoveWithMetaData.pokemonMove.name.capitalize()
        levelLearnMethod.text = root.context.getString(
                R.string.learnt_by,
                pokemonMoveWithMetaData.pokemonMoveMetaData.learnMethods.names())
        generationText.text = PokemonGeneration.formatGenerationName(generation)
        powerText.text = pokemonMoveWithMetaData.pokemonMove.power.toString()
        accuracyText.text = binding.root.context.getString(R.string.move_accuracy,
                pokemonMoveWithMetaData.pokemonMove.accuracy)
        ppText.text = pokemonMoveWithMetaData.pokemonMove.pp.toString()
    }

    private fun PokemonMoveViewHolderBinding.setExpanded(pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        setUpMoveLearningAdapter(pokemonMoveWithMetaData)
        setUpLearningRecyclerView()
        setMotionLayoutExpandedStateProgress(1f)
        showMoreLessToggleButton.rotation = 180f
        setShowMoreLessTextToLess()
    }

    private fun PokemonMoveViewHolderBinding.setNotExpanded() {
        setMotionLayoutExpandedStateProgress(0f)
        showMoreLessToggleButton.rotation = 0f
        setShowMoreLessTextToMore()
    }

    private fun PokemonMoveViewHolderBinding.setShowMoreLessTextToMore() {
        showMoreLessToggle.text = root.context.getString(R.string.show_more)
        showMoreLessToggleButton.contentDescription = root.context.getString(R.string.show_more)
    }

    private fun PokemonMoveViewHolderBinding.setShowMoreLessTextToLess() {
        showMoreLessToggle.text = root.context.getString(R.string.show_less)
        showMoreLessToggleButton.contentDescription = root.context.getString(R.string.show_less)
    }

    private fun PokemonMoveViewHolderBinding.showTableAndHeader() {
        levelLearnedAtTextView.visibility = View.VISIBLE
        levelLearnedAtTableHeader.root.visibility = View.VISIBLE
        levelLearnedAtTable.visibility = View.VISIBLE
    }

    private fun setChipsAndRibbons(pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        CoroutineScope(Dispatchers.Default).launch {
            val pokemonMoveTypeOrCategoryList = pokemonMoveWithMetaData.pokemonMove.typeOrCategoryList()
            buildPokemonMoveTypeAndCategoryChips(pokemonMoveTypeOrCategoryList)
            buildPokemonMoveTypeAndCategoryRibbons(pokemonMoveTypeOrCategoryList)
        }
    }

    private fun setMotionLayoutExpandedStateProgress(progress: Float) {
        if (ViewCompat.isLaidOut(binding.moveViewHolderRoot)) {
            binding.moveViewHolderRoot.progress = progress
        } else {
            binding.moveViewHolderRoot.post { binding.moveViewHolderRoot.progress = progress }
        }
    }

}