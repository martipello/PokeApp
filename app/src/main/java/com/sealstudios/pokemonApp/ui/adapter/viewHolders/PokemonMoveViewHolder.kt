package com.sealstudios.pokemonApp.ui.adapter.viewHolders

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonMoveWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonMoveViewHolderBinding
import com.sealstudios.pokemonApp.ui.adapter.MoveLearningAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveLearning
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveTypeOrCategory
import com.sealstudios.pokemonApp.ui.util.PokemonCategory
import com.sealstudios.pokemonApp.ui.util.PokemonCategory.Companion.getCategoryForDamageClass
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypeForPokemonType
import com.sealstudios.pokemonApp.ui.util.TypesAndCategoryGroupHelper
import com.sealstudios.pokemonApp.ui.util.decorators.JustBottomDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class PokemonMoveViewHolder
constructor(
    private val binding: PokemonMoveViewHolderBinding,
    private val clickListener: PokemonMoveAdapterClickListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var learningAdapter : MoveLearningAdapter

    fun bind(pokemonMoveWithMetaData: PokemonMoveWithMetaData, isExpanded: Boolean = false) = with(binding) {
        animateToggle(isExpanded)
        showLearningTable(isExpanded, pokemonMoveWithMetaData)
        val generation = PokemonGeneration.getGeneration(pokemonMoveWithMetaData.pokemonMove.generation)

        populateTextViews(pokemonMoveWithMetaData, generation)

        showMoreLessToggle.setOnClickListener {
            clickListener?.onItemSelected(bindingAdapterPosition, pokemonMoveWithMetaData.pokemonMove)
        }
        showMoreLessToggleButton.setOnClickListener {
            clickListener?.onItemSelected(bindingAdapterPosition, pokemonMoveWithMetaData.pokemonMove)
        }

        CoroutineScope(Dispatchers.Default).launch {
            val pokemonMoveTypeOrCategoryList = getPokemonMoveTypeOrCategoryList(pokemonMoveWithMetaData.pokemonMove)
            buildPokemonMoveTypeAndCategoryChips(pokemonMoveTypeOrCategoryList, binding)
            buildPokemonMoveTypeAndCategoryRibbons(pokemonMoveTypeOrCategoryList, binding)
        }
    }

    private fun showLearningTable(isExpanded: Boolean, pokemonMoveWithMetaData: PokemonMoveWithMetaData){
        if (isExpanded) {
            setUpMoveLearningAdapter(pokemonMoveWithMetaData)
            setUpLearningRecyclerView()
        } else {
            binding.levelLearnedAtTextView.visibility = View.GONE
            binding.levelLearnedAtTableHeader.root.visibility = View.GONE
            binding.levelLearnedAtTable.visibility = View.GONE
        }
    }

    private fun setUpLearningRecyclerView() {
        binding.levelLearnedAtTable.apply {
            adapter = learningAdapter
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
        if (moveLearningList.isNotEmpty()){
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

    private fun animateToggle(isExpanded: Boolean) {
        if (isExpanded) {
            rotateToggleOpen()
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_less)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_less)
            binding.expandedContent.visibility = View.VISIBLE
        } else {
            rotateToggleClose()
            binding.showMoreLessToggle.text = binding.root.context.getString(R.string.show_more)
            binding.showMoreLessToggleButton.contentDescription = binding.root.context.getString(R.string.show_more)
            binding.expandedContent.visibility = View.GONE
        }
    }

    //TODO work on this so its not always animating check before hand

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
        TypesAndCategoryGroupHelper(
            binding.dualTypeChipLayout.pokemonTypesChipGroup,
            pokemonMoveTypeOrCategory
        ).bindChips()
    }

    private suspend fun buildPokemonMoveTypeAndCategoryRibbons(
        pokemonMoveTypeOrCategory: List<PokemonMoveTypeOrCategory>,
        binding: PokemonMoveViewHolderBinding
    ) {
        withContext(Dispatchers.Default){
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
        withContext(Dispatchers.Main){
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
        withContext(Dispatchers.Main){
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