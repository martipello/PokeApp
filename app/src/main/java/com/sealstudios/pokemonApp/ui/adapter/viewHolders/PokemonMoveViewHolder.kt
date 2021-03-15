package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonMoveWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.MoveLearningAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveLearning
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveTypeOrCategory
import com.sealstudios.pokemonApp.ui.extensions.removeItemDecorations
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitTransitionEnd
import com.sealstudios.pokemonApp.ui.util.*
import com.sealstudios.pokemonApp.ui.util.PokemonCategory.Companion.getCategoryForDamageClass
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypeForPokemonType
import com.sealstudios.pokemonApp.ui.util.decorators.JustBottomDecoration
import kotlinx.coroutines.*
import java.util.*


class PokemonMoveViewHolder
constructor(
        private val binding: PokemonMoveViewHolderBinding,
        private val clickListener: PokemonMoveAdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var learningAdapter: MoveLearningAdapter
//    private var myExpanded = false

    init {
        binding.root.doOnPreDraw {
            setProgress(binding.moveViewHolderRoot, 0f)
        }
    }

    fun bind(pokemonMoveWithMetaData: PokemonMoveWithMetaData, isExpanded: Boolean = false) = with(binding) {

        setToggleState(isExpanded)
        setExpandedState(isExpanded)

        showLearningTable(isExpanded, pokemonMoveWithMetaData)
        val generation = PokemonGeneration.getGeneration(pokemonMoveWithMetaData.pokemonMove.generation)

        populateTextViews(pokemonMoveWithMetaData, generation)

        showMoreLessToggle.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                showMoreLessClickAction(isExpanded, pokemonMoveWithMetaData)
            }
        }
        showMoreLessToggleButton.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                showMoreLessClickAction(isExpanded, pokemonMoveWithMetaData)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            val pokemonMoveTypeOrCategoryList = getPokemonMoveTypeOrCategoryList(pokemonMoveWithMetaData.pokemonMove)
            buildPokemonMoveTypeAndCategoryChips(pokemonMoveTypeOrCategoryList, binding)
            buildPokemonMoveTypeAndCategoryRibbons(pokemonMoveTypeOrCategoryList, binding)
        }
    }

    private fun setToggleState(isExpanded: Boolean) {
        if (isExpanded) {
            binding.showMoreLessToggleButton.rotation = 180f
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_less)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_less)
        } else {
            binding.showMoreLessToggleButton.rotation = 0f
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_more)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_more)
        }
    }

    private fun setExpandedState(isExpanded: Boolean) {
        if (isExpanded) {
            setProgress(binding.moveViewHolderRoot, 1f)
        } else {
            setProgress(binding.moveViewHolderRoot, 0f)
        }
    }

    private fun setProgress(motionLayout: MotionLayout, progress: Float) {
        if (ViewCompat.isLaidOut(motionLayout)) {
            motionLayout.progress = progress
        } else {
            motionLayout.post { motionLayout.progress = progress }
        }
    }

    private suspend fun showMoreLessClickAction(isExpanded: Boolean, pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        withContext(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                showLearningTable(!isExpanded, pokemonMoveWithMetaData)
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
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_less)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_less)
        } else {
            rotateToggleClose()
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_more)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_more)
        }
    }

    private fun animateExpandedContent(isExpanded: Boolean) {
        if (isExpanded) {
            binding.moveViewHolderRoot.transitionToStart()
        } else {
            binding.moveViewHolderRoot.transitionToEnd()
        }
    }

    private fun showLearningTable(isExpanded: Boolean, pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        if (isExpanded) {
            setUpMoveLearningAdapter(pokemonMoveWithMetaData)
            setUpLearningRecyclerView()
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

    private fun setUpMoveLearningAdapter(pokemonMoveWithMetaData: PokemonMoveWithMetaData) {
        learningAdapter = MoveLearningAdapter()
        val learnMethods = pokemonMoveWithMetaData.pokemonMoveMetaData.learnMethods
        val learnLevels = pokemonMoveWithMetaData.pokemonMoveMetaData.levelsLearnedAt
        val generations = pokemonMoveWithMetaData.pokemonMoveMetaData.versionsLearnt

        val moveLearningList = mutableListOf<MoveLearning>()
        for (i in learnMethods.indices) {
            moveLearningList.add(
                    MoveLearning(
                            generation = generations.getOrElse(index = i, defaultValue = { "" }),
                            learntAt = learnLevels.getOrElse(index = i, defaultValue = { 0 }),
                            learntBy = learnMethods[i]
                    )
            )
        }
        if (moveLearningList.isNotEmpty()) {
            binding.levelLearnedAtTextView.visibility = View.VISIBLE
            binding.levelLearnedAtTableHeader.root.visibility = View.VISIBLE
            binding.levelLearnedAtTable.visibility = View.VISIBLE
        }
        learningAdapter.submitList(moveLearningList.toSet().toList())
    }

    @SuppressLint("StringFormatInvalid")
    private fun PokemonMoveViewHolderBinding.populateTextViews(
            pokemonMoveWithMetaData: PokemonMoveWithMetaData,
            generation: PokemonGeneration
    ) {
        description.text = pokemonMoveWithMetaData.pokemonMove.description.replace("\\s".toRegex(), " ")
        pokemonMoveNameTextView.text = pokemonMoveWithMetaData.pokemonMove.name.capitalize(Locale.ROOT)

        levelLearnMethod.text = binding.root.context.getString(
                R.string.learnt_by,
                pokemonMoveWithMetaData.pokemonMoveMetaData.learnMethods.toSet().joinToString {
                    it.capitalize(
                            Locale.ROOT
                    )
                })

        generationText.text = PokemonGeneration.formatGenerationName(generation)
        powerText.text = pokemonMoveWithMetaData.pokemonMove.power.toString()
        accuracyText.text =
                binding.root.context.getString(R.string.move_accuracy, pokemonMoveWithMetaData.pokemonMove.accuracy)
        ppText.text = pokemonMoveWithMetaData.pokemonMove.pp.toString()
    }

    private fun rotateToggleOpen() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(180f)
    }

    private fun rotateToggleClose() {
        binding.showMoreLessToggleButton.animate().setDuration(200).rotation(0f)
    }

    private suspend fun getPokemonMoveTypeOrCategoryList(pokemonMove: PokemonMove): List<PokemonMoveTypeOrCategory> {
        return withContext(context = Dispatchers.Default) {
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

    private suspend fun buildPokemonMoveTypeAndCategoryChips(
            pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>,
            binding: PokemonMoveViewHolderBinding
    ) {
        withContext(Dispatchers.Default) {
            TypesAndCategoryGroupHelper(
                    binding.dualTypeChipLayout.pokemonTypesChipGroup,
                    pokemonMoveTypeOrCategory
            ).bindChips()
        }
    }

    private suspend fun buildPokemonMoveTypeAndCategoryRibbons(
            pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>,
            binding: PokemonMoveViewHolderBinding
    ) {
        withContext(Dispatchers.Default) {
            for (x in pokemonMoveTypeOrCategory.indices) {
                val pokemonTypeOrCategory = pokemonMoveTypeOrCategory[x]
                if (pokemonTypeOrCategory.itemType == PokemonType.itemType) {
                    pokemonMoveTypeOrCategory[x].type?.let {
                        setPokemonMoveTypeRibbon(binding, it)
                    }
                } else {
                    pokemonMoveTypeOrCategory[x].category?.let {
                        setPokemonMoveCategoryRibbon(binding, it)
                    }
                }
            }
        }
    }

    private suspend fun setPokemonMoveCategoryRibbon(
            binding: PokemonMoveViewHolderBinding,
            it: PokemonCategory
    ) {
        withContext(Dispatchers.Main) {
            binding.categoryRibbon.colorFilter = null
            binding.categoryRibbon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, it.color),
                    PorterDuff.Mode.SRC_IN
            )
            binding.categoryRibbon.visibility = View.VISIBLE
        }
    }

    private suspend fun setPokemonMoveTypeRibbon(
            binding: PokemonMoveViewHolderBinding,
            it: PokemonType
    ) {
        withContext(Dispatchers.Main) {
            binding.typeRibbon.colorFilter = null
            binding.typeRibbon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, it.color),
                    PorterDuff.Mode.SRC_IN
            )
            binding.typeRibbon.visibility = View.VISIBLE
        }
    }

    companion object {
        const val layoutType = 1000
    }

}