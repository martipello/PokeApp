package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.highResPokemonUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves.Companion.getPokemonMoves
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonMoveAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.insets.PokemonDetailFragmentInsets
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitTransitionEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.startAndWait
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.createPokemonTypeChip
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypesForPokemonTypes
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.resume

@AndroidEntryPoint
class PokemonDetailFragment : Fragment(),
    PokemonMoveAdapterClickListener {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String
    private var pokemonId: Int = -1
    private val args: PokemonDetailFragmentArgs by navArgs()
    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()
    private lateinit var pokemonMoveAdapter: PokemonMoveAdapter
    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private var hasExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBackButtonCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        handleNavigationArgs()
        observeUIColor()
        setUpPokemonAdapter()
        setUpPokemonMovesRecyclerView()
        observeHasExpandedState()
        setViewModelProperties()
        PokemonDetailFragmentInsets().setInsets(binding)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            setPokemonImageView(highResPokemonUrl(pokemonId))
            handleEnterAnimation()
            observePokemon()
        }
    }

    private suspend fun handleEnterAnimation(): Boolean = suspendCancellableCoroutine { continuation ->
        viewLifecycleOwner.lifecycleScope.launch {
            sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)
            (sharedElementEnterTransition as TransitionSet).run {
                awaitTransitionEnd {
                    startPostponedEnterTransition()
                }
            }
            binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(
                R.id.large_image
            )
            val x = binding.splash.right / 2
            val location = IntArray(2)
            binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView
                .getLocationOnScreen(location)
            val y = location[1] + binding.pokemonImageViewHolderLayout
                .pokemonBackgroundCircleView.height / 2
            binding.splash.circleReveal(null, startAtX = x, startAtY = y).run {
                startAndWait()
                binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder
                    .setCardBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            android.R.color.transparent
                        )
                    )
                awaitEnd()
            }
            pokemonDetailViewModel.setRevealAnimationExpandedState(true)
            continuation.resume(true)
        }
    }

    private fun handleExitAnimation() {
        viewLifecycleOwner.lifecycleScope.launch {
            val x: Int = binding.splash.right / 2
            val location = IntArray(2)
            binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.getLocationOnScreen(
                location
            )
            val y =
                location[1] + binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.height / 2
            binding.splash.circleHide(null, endAtX = x, endAtY = y).run {
                startAndWait()
                delay(150)
                binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToStart()
                popDelayed()
                awaitEnd()
                binding.splash.visibility = View.INVISIBLE
            }
        }
    }

    private fun addBackButtonCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleExitAnimation()
            this.remove()
        }
    }

    private suspend fun popDelayed() {
        withContext(Dispatchers.Main) {
            delay(100)
            findNavController().popBackStack()
        }
    }

    private fun handleNavigationArgs() {
        pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        pokemonName = args.pokemonName
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.transitionName =
            args.transitionName
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.setCardBackgroundColor(
            args.dominantSwatchRgb
        )
    }

    private fun setViewModelProperties() {
        pokemonDetailViewModel.setId(pokemonId)
        pokemonDetailViewModel.setViewColors(
            args.dominantSwatchRgb,
            args.lightVibrantSwatchRgb
        )
    }

    private fun observePokemon() {
        pokemonDetailViewModel.pokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            viewLifecycleOwner.lifecycleScope.launch {
                pokemon?.let {
                    populateViews(it)
                }
            }
        })
    }

    private fun observeHasExpandedState() {
        pokemonDetailViewModel.revealAnimationExpanded.observe(
            viewLifecycleOwner,
            Observer { hasExpanded ->
                this.hasExpanded = hasExpanded
            }
        )
    }

    private fun observeUIColor() {
        pokemonDetailViewModel.dominantAndLightVibrantColors.observe(
            viewLifecycleOwner,
            Observer { viewColors ->
                setColoredElements(
                    viewColors.dominantColor,
                    viewColors.lightVibrantColor
                )
                if (hasExpanded) {
                    restoreUIState()
                }
            })
    }

    private fun setColoredElements(dominantColor: Int, lightVibrantSwatchRgb: Int) {
        binding.splash.setBackgroundColor(dominantColor)
        binding.squareangleMask.setColorFilter(lightVibrantSwatchRgb)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.setCardBackgroundColor(
            dominantColor
        )
    }

    private fun restoreUIState() {
        binding.splash.visibility = View.VISIBLE
        binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(
            R.id.large_image
        )
        binding.root.post {
//            createRevealAnimation()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setActionBar() {
        binding.toolbar.outlineProvider = null
        binding.appBarLayout.outlineProvider = null
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpPokemonAdapter() {
        pokemonMoveAdapter = PokemonMoveAdapter(clickListener = this)
    }

    @SuppressLint("DefaultLocale")
    private fun populateViews(pokemon: PokemonWithTypesAndSpeciesAndMoves?) {
        viewLifecycleOwner.lifecycleScope.launch {
            pokemon?.let {
                binding.mainProgress.visibility = View.GONE
                setPokemonTypes(it.types)
                setPokemonFormData(it)
                binding.content.visibility = View.VISIBLE
                setPokemonMoves(it.moves.getPokemonMoves())
            }
        }
    }

    private suspend fun setPokemonImageView(imageUrl: String): Boolean = suspendCancellableCoroutine { continuation ->
            val requestOptions =
                RequestOptions.noTransformation()
            glide.asBitmap()
                .load(imageUrl)
                .apply(requestOptions)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        continuation.resume(false)
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        binding.pokemonImageViewHolderLayout.pokemonImageView.setImageBitmap(
                            resource
                        )
                        continuation.resume(true)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        continuation.resume(true)
                    }
                })
        }

    // ------------ populate Pokemon data views ------------ //

    private fun setPokemonTypes(
        pokemonTypes: List<PokemonType>
    ) {
        binding.pokemonTypesChipGroup.removeAllViews()
        val types = getPokemonEnumTypesForPokemonTypes(
            PokemonType.getTypesInOrder(types = pokemonTypes)
        )

        for (type in types) {
            binding.pokemonTypesChipGroup.addView(
                createPokemonTypeChip(type, binding.root.context)
            )
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setPokemonFormData(
        it: PokemonWithTypesAndSpeciesAndMoves
    ) {
        val context = binding.root.context
        binding.title.text = it.pokemon.name.capitalize()
        binding.subtitle.text = it.species.species.capitalize()
        binding.genTextView.text = context.getString(R.string.generation, it.species.generation)
        binding.idLabel.text = context.getString(R.string.pokemonId, it.pokemon.id)
        binding.heightTextView.text = context.getString(R.string.height, it.pokemon.height)
        binding.weightTextView.text = context.getString(R.string.weight, it.pokemon.weight)
        binding.pokedexSubtitle.text =
            context.getString(R.string.pok_dex_gen, it.species.pokedex?.capitalize())
        binding.pokedexEntryText.text = it.species.pokedexEntry
        binding.shapeText.text =
            context.getString(R.string.shape_text, it.species.shape?.capitalize())
        binding.formDescriptionText.text =
            context.getString(R.string.form_text, it.species.formDescription)
        binding.habitatText.text =
            context.getString(R.string.habitat, it.species.habitat?.capitalize())
    }

    private suspend fun setPokemonMoves(
        pokemonMoves: Map<String, List<PokemonMove>?>
    ) {

        val pokemonMoveList = mutableListOf<PokemonMove>()

        Log.d("DETAIL", "setPokemonMoves $pokemonMoves")
        for (moveEntry in pokemonMoves.entries) {
            if (!moveEntry.value.isNullOrEmpty()) {
                pokemonMoveList.addAll(moveEntry.value!!)
            }
//                    moveEntry.value?.forEach { move ->
//                        Log.d("DETAIL", "move key $move")
//                    }
        }
        pokemonMoveAdapter.submitList(pokemonMoveList)

        binding.pokemonMovesLoading.visibility = View.GONE

        if (pokemonMoveList.isEmpty()) {
            binding.pokemonMovesEmptyText.visibility = View.VISIBLE
        } else {
            binding.pokemonMovesEmptyText.visibility = View.GONE
        }
    }

    private fun setUpPokemonMovesRecyclerView() {
        binding.pokemonMoveRecyclerView.apply {
            adapter = pokemonMoveAdapter
            addPokemonMovesRecyclerViewDecoration(this)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun addPokemonMovesRecyclerViewDecoration(
        recyclerView: RecyclerView
    ) {
        recyclerView.addItemDecoration(
            PokemonListDecoration(
                recyclerView.context.resources.getDimensionPixelSize(
                    R.dimen.qualified_small_margin_8dp
                )
            )
        )
    }

    override fun onItemSelected(position: Int, pokemonMove: PokemonMove) {
        TODO("Not yet implemented")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleExitAnimation()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}






