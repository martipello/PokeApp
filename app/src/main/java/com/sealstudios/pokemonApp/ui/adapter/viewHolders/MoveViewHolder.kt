package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Move.Companion.typeOrCategoryList
import com.sealstudios.pokemonApp.database.`object`.MoveMetaData.Companion.moveLearningList
import com.sealstudios.pokemonApp.database.`object`.wrappers.MoveWithMetaData
import com.sealstudios.pokemonApp.databinding.MoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.MoveLearningAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.MoveAdapterClickListener
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

class MoveViewHolder
constructor(
        private val binding: MoveViewHolderBinding,
        private val clickListener: MoveAdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var learningAdapter: MoveLearningAdapter

    fun bind(moveWithMetaData: MoveWithMetaData, isExpanded: Boolean = false) = with(binding) {

        if (isExpanded) {
            setExpanded()
        } else {
            setNotExpanded()
        }

        populateTextViews(moveWithMetaData)
        setChipsAndRibbons(moveWithMetaData)

        showMoreLessToggle.setOnClickListener {
            onExpandCollapse(isExpanded, moveWithMetaData)
        }

        showMoreLessToggleButton.setOnClickListener {
            onExpandCollapse(isExpanded, moveWithMetaData)
        }
    }

    private fun onExpandCollapse(isExpanded: Boolean, moveWithMetaData: MoveWithMetaData) {
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.Main) {
                if (!isExpanded) {
                    setUpMoveLearningTable(moveWithMetaData)
                }
                animateToggle(!isExpanded)
                animateExpandedContent(isExpanded)
            }
            binding.moveViewHolderRoot.awaitTransitionEnd()
            withContext(Dispatchers.Main) {
                clickListener?.onItemSelected(bindingAdapterPosition, moveWithMetaData.move)
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

    private fun setUpMoveLearningTable(moveWithMetaData: MoveWithMetaData) {
        setUpMoveLearningAdapter(moveWithMetaData)
        setUpLearningRecyclerView()
    }

    private fun setUpMoveLearningAdapter(moveWithMetaData: MoveWithMetaData) {
        learningAdapter = MoveLearningAdapter()
        CoroutineScope(Dispatchers.Default).launch {
            val moveLearningList = moveWithMetaData.moveMetaData.moveLearningList()

            if (moveLearningList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    binding.showTableAndHeader()
                    learningAdapter.submitList(moveLearningList)
                }
            }
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
    private fun MoveViewHolderBinding.populateTextViews(
            moveWithMetaData: MoveWithMetaData
    ) {
        val generation = PokemonGeneration.getGeneration(moveWithMetaData.move.generation)
        val description = moveWithMetaData.move.description.removeWhiteSpace()

        descriptionText.text = description
        pokemonMoveNameTextView.text = moveWithMetaData.move.name.capitalize()
        levelLearnMethod.text = root.context.getString(
                R.string.learnt_by,
                moveWithMetaData.moveMetaData.learnMethods.names())
        generationText.text = PokemonGeneration.formatGenerationName(generation)
        powerText.text = moveWithMetaData.move.power.toString()
        accuracyText.text = root.context.getString(R.string.move_accuracy,
                moveWithMetaData.move.accuracy)
        ppText.text = moveWithMetaData.move.pp.toString()
    }

    private fun MoveViewHolderBinding.setExpanded() {
        showTableAndHeader()
        setMotionLayoutExpandedStateProgress(1f)
        showMoreLessToggleButton.rotation = 180f
        setShowMoreLessTextToLess()
    }

    private fun MoveViewHolderBinding.setNotExpanded() {
        setMotionLayoutExpandedStateProgress(0f)
        showMoreLessToggleButton.rotation = 0f
        setShowMoreLessTextToMore()
    }

    private fun MoveViewHolderBinding.setShowMoreLessTextToMore() {
        showMoreLessToggle.text = root.context.getString(R.string.show_more)
        showMoreLessToggleButton.contentDescription = root.context.getString(R.string.show_more)
    }

    private fun MoveViewHolderBinding.setShowMoreLessTextToLess() {
        showMoreLessToggle.text = root.context.getString(R.string.show_less)
        showMoreLessToggleButton.contentDescription = root.context.getString(R.string.show_less)
    }

    private fun MoveViewHolderBinding.showTableAndHeader() {
        levelLearnedAtTextView.visibility = View.VISIBLE
        levelLearnedAtTableHeader.root.visibility = View.VISIBLE
        levelLearnedAtTable.visibility = View.VISIBLE
    }

    private fun setChipsAndRibbons(moveWithMetaData: MoveWithMetaData) {
        CoroutineScope(Dispatchers.Default).launch {
            val pokemonMoveTypeOrCategoryList = moveWithMetaData.move.typeOrCategoryList()
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