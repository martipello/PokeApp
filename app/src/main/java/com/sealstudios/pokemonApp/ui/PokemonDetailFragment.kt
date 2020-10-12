package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.FabFilterAnimationListener
import com.sealstudios.pokemonApp.ui.insets.PokemonDetailFragmentInsets
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.createPokemonTypeChip
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypesForPokemonTypes
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PokemonDetailFragment : Fragment(),
    PokemonMoveAdapterClickListener, FabFilterAnimationListener {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String

    private val args: PokemonDetailFragmentArgs by navArgs()
    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()
    private lateinit var pokemonMoveAdapter: PokemonMoveAdapter
    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private var transitionListenerAdapter: TransitionListenerAdapter? = null
    private var hasExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBackButtonCallback()
    }

    private fun addBackButtonCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            startExitTransition()
            this.remove()
        }
    }

    private fun startExitTransition() {
        createHideAnimation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        setAndPostponeEnterAnimation()
        PokemonDetailFragmentInsets().setInsets(binding)
        handleNavigationArgs()
        setUpPokemonAdapter()
        setUpPokemonMovesRecyclerView()
        observeHasExpandedState()
        observeUIColor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        Log.d("DETAIL", "onViewCreated")
        observePokemon()
    }

    private fun setAndPostponeEnterAnimation() {
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
        (sharedElementEnterTransition as TransitionSet).addListener(sharedElementListener())
    }

    private fun startPostponedEnterAnimation() {
        Log.d("DETAIL", "startPostponedEnterAnimation")
        startPostponedEnterTransition()
    }

    private fun handleNavigationArgs() {
        val pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        pokemonName = args.pokemonName
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.transitionName =
            args.transitionName
        setPokemonImageView(highResPokemonUrl(pokemonId))
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.setCardBackgroundColor(
            args.dominantSwatchRgb
        )
        setViewModelProperties(pokemonId)
    }

    private fun setViewModelProperties(pokemonId: Int) {
        pokemonDetailViewModel.setId(pokemonId)
        pokemonDetailViewModel.setViewColors(
            args.dominantSwatchRgb,
            args.lightVibrantSwatchRgb
        )
    }

    private fun observePokemon() {
        Log.d("DETAIL", "observePokemon")
        pokemonDetailViewModel.pokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            Log.d("DETAIL", "pokemon.observe")
            pokemon?.let {
                populateViews(it)
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
        transitionPokemonImageFromSmallToLarge(binding)
        binding.root.post {
            createRevealAnimation()
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
        Log.d("DETAIL", "populateViews")
        pokemon?.let {
            with(binding) {
                setPokemonTypes(it.types)
                setPokemonFormData(it)
            }

            CoroutineScope(Dispatchers.Default).launch {
                setPokemonMoves(it.moves.getPokemonMoves())
            }
        }
    }

    private fun setPokemonImageView(imageUrl: String) {
        val requestOptions =
            RequestOptions.noTransformation()
        glide.asBitmap()
            .load(imageUrl)
            .apply(requestOptions)
            .addListener(createRequestListener())
            .into(binding.pokemonImageViewHolderLayout.pokemonImageView)
    }

    private fun createRequestListener(): RequestListener<Bitmap?> {
        return object : RequestListener<Bitmap?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Bitmap?>,
                isFirstResource: Boolean
            ): Boolean {
                glide.asBitmap()
                    .load(R.drawable.pokeball_vector)
                    .into(binding.pokemonImageViewHolderLayout.pokemonImageView)
                startPostponedEnterAnimation()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterAnimation()
                return false
            }
        }
    }

    private fun createRevealAnimation() {
        val x = binding.splash.right / 2
        val location = IntArray(2)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.getLocationOnScreen(
            location
        )
        val y =
            location[1] + binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.height / 2
        binding.splash.circleReveal(this, startAtX = x, startAtY = y)
    }

    private fun createHideAnimation() {
        val x: Int = binding.splash.right / 2
        val location = IntArray(2)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.getLocationOnScreen(
            location
        )
        val y =
            location[1] + binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.height / 2
        binding.splash.circleHide(this, endAtX = x, endAtY = y)
    }

    // ------------ Animation Listeners ------------ //

    private fun sharedElementListener(): TransitionListenerAdapter {
        return transitionListenerAdapter ?: object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                _binding?.let {
                    if (!hasExpanded) {
                        transitionPokemonImageFromSmallToLarge(it)
                        createRevealAnimation()
                    }
                }
            }
        }
    }

    private fun transitionPokemonImageFromSmallToLarge(it: PokemonDetailFragmentBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            it.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(
                R.id.large_image
            )
        }
    }

    private fun removeSharedElementListener() {
        (sharedElementEnterTransition as TransitionSet).removeListener(sharedElementListener())
    }

    // ------------ Animation Listeners ------------ //

    private suspend fun popDelayed() {
        withContext(Dispatchers.Main) {
            delay(100)
            findNavController().popBackStack()
        }
    }

    // ------------ populate Pokemon data views ------------ //

    private fun PokemonDetailFragmentBinding.setPokemonTypes(
        pokemonTypes: List<PokemonType>
    ) {
        pokemonTypesChipGroup.removeAllViews()
        val types = getPokemonEnumTypesForPokemonTypes(
            PokemonType.getTypesInOrder(types = pokemonTypes)
        )

        for (type in types) {
            pokemonTypesChipGroup.addView(
                createPokemonTypeChip(type, binding.root.context)
            )
        }
    }

    @SuppressLint("DefaultLocale")
    private fun PokemonDetailFragmentBinding.setPokemonFormData(
        it: PokemonWithTypesAndSpeciesAndMoves
    ) {
        val context = binding.root.context
        title.text = it.pokemon.name.capitalize()
        subtitle.text = it.species.species.capitalize()
        genTextView.text = context.getString(R.string.generation, it.species.generation)
        idLabel.text = context.getString(R.string.pokemonId, it.pokemon.id)
        heightTextView.text = context.getString(R.string.height, it.pokemon.height)
        weightTextView.text = context.getString(R.string.weight, it.pokemon.weight)
        pokedexSubtitle.text =
            context.getString(R.string.pok_dex_gen, it.species.pokedex?.capitalize())
        pokedexEntryText.text = it.species.pokedexEntry
        shapeText.text = context.getString(R.string.shape_text, it.species.shape?.capitalize())
        formDescriptionText.text = context.getString(R.string.form_text, it.species.formDescription)
        habitatText.text = context.getString(R.string.habitat, it.species.habitat?.capitalize())
    }

    private suspend fun setPokemonMoves(
        pokemonMoves: Map<String, List<PokemonMove>?>
    ) {
        val scope = CoroutineScope(Dispatchers.Main)
        val pokemonMoveList = mutableListOf<PokemonMove>()

        scope.launch {
            withContext(Dispatchers.Default) {
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
            }

            binding.pokemonMovesLoading.visibility = View.GONE

            if (pokemonMoveList.isEmpty()) {
                binding.pokemonMovesEmptyText.visibility = View.VISIBLE
            } else {
                binding.pokemonMovesEmptyText.visibility = View.GONE
            }
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

    // ------------ populate Pokemon data views ------------ //

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startExitTransition()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeSharedElementListener()
        transitionListenerAdapter = null
        _binding = null
    }

    override fun onItemSelected(position: Int, pokemonMove: PokemonMove) {
        TODO("Not yet implemented")
    }

    override fun onSlideHideAnimationStarted() {
        TODO("Not yet implemented")
    }

    override fun onSlideHideAnimationFinished() {
        TODO("Not yet implemented")
    }

    override fun onCircleRevealAnimationStarted() {
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder
            .setCardBackgroundColor(
                ContextCompat.getColor(binding.root.context, android.R.color.transparent)
            )
    }

    override fun onCircleRevealAnimationFinished() {
        pokemonDetailViewModel.setRevealAnimationExpandedState(true)
    }

    override fun onCircleHideAnimationStarted() {
        lifecycleScope.launch {
            delay(150)
            binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToStart()
            popDelayed()
        }
    }

    override fun onCircleHideAnimationFinished() {
        binding.splash.visibility = View.INVISIBLE
    }

    override fun onArcAnimationStarted() {
        TODO("Not yet implemented")
    }

    override fun onArcAnimationFinished() {
        TODO("Not yet implemented")
    }

}

