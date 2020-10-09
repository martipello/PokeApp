package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
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
import com.sealstudios.pokemonApp.ui.adapter.layoutManagers.NoScrollLayoutManager
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.util.*
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.createPokemonTypeChip
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypesForPokemonTypes
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.hypot

@AndroidEntryPoint
class PokemonDetailFragment : Fragment(),
    PokemonMoveAdapterClickListener {

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
        setInsets()
        handleNavigationArgs()
        observeHasExpandedState()
        observeUIColor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        setUpPokemonAdapter()
        setUpPokemonRecyclerView()
        observePokemon()
    }

    private fun setAndPostponeEnterAnimation() {
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
        (sharedElementEnterTransition as TransitionSet).addListener(sharedElementListener())

    }

    private fun setInsets() {
        binding.appBarLayout.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            view.layoutParams = marginLayoutParams
        }
        binding.toolbar.doOnApplyWindowInsetPadding { view, windowInsets, initialPadding ->
            view.updatePadding(
                left = windowInsets.systemWindowInsetLeft + initialPadding.left,
                right = windowInsets.systemWindowInsetRight + initialPadding.right
            )
        }
        binding.toolbarLayout.doOnApplyWindowInsetPadding { _, _, _ ->
            //required or the views below do not get there padding updated
        }
        binding.detailRoot.doOnApplyWindowInsetPadding { _, _, _ ->
            //required or the views below do not get there padding updated
        }
        binding.scrollView.doOnApplyWindowInsetPadding { view, windowInsets, initialPadding ->
            view.updatePadding(
                bottom = windowInsets.systemWindowInsetBottom + initialPadding.bottom
            )
        }
    }

    private fun handleNavigationArgs() {
        pokemonName = args.pokemonName
        binding.pokemonImageViewHolderLayout.pokemonImageViewHolder.transitionName =
            args.transitionName
        val pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        setViewModelProperties(pokemonId)
        val imageUrl = highResPokemonUrl(pokemonId)
        setPokemonImageView(imageUrl)
    }

    private fun setViewModelProperties(pokemonId: Int) {
        pokemonDetailViewModel.setId(pokemonId)
        pokemonDetailViewModel.setViewColors(
            args.dominantSwatchRgb,
            args.lightVibrantSwatchRgb
        )
    }

    private fun observePokemon() {
        pokemonDetailViewModel.pokemon.observe(viewLifecycleOwner, Observer { pokemon ->
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
        binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(R.id.large_image)
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
        pokemon?.let {
            with(binding) {
                setPokemonTypes(it.types)
                setPokemonFormData(it)
                setPokemonMoves(it.moves.getPokemonMoves())
            }
        }
    }

    private fun setPokemonImageView(imageUrl: String) {
        val requestOptions = RequestOptions.placeholderOf(R.drawable.pokeball_vector).dontTransform()
        glide.asBitmap()
            .load(imageUrl)
            .apply(requestOptions)
            .dontTransform()
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
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }
        }
    }

    // ------------ Animations ------------ //

    private fun createRevealAnimation() {
        val x = binding.splash.right / 2
        val location = IntArray(2)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.getLocationOnScreen(
            location
        )
        val y =
            location[1].toFloat() + binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.height / 2

        val endRadius =
            hypot(
                binding.splash.width.toDouble(),
                binding.splash.height.toDouble()
            ).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.splash, x, y.toInt(),
            0f,
            endRadius.toFloat()
        )

        binding.splash.visibility = View.VISIBLE
        anim.addListener(getRevealSplashListener())
        anim.start()
    }

    private fun createHideAnimation() {
        val x: Int = binding.splash.right / 2
        val location = IntArray(2)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.getLocationOnScreen(
            location
        )
        val y =
            location[1].toFloat() + binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.height / 2


        val startRadius =
            hypot(
                binding.splash.width.toDouble(),
                binding.splash.height.toDouble()
            ).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.splash, x, y.toInt(),
            startRadius.toFloat(),
            90.dp.toFloat()
        )

        anim.addListener(getHideSplashListener())
        anim.start()
    }

    // ------------ Animations ------------ //

    // ------------ Animation Listeners ------------ //

    private fun sharedElementListener(): TransitionListenerAdapter {
        return transitionListenerAdapter ?: object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                _binding?.let {
                    if (!hasExpanded) {
                        it.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(
                            R.id.large_image
                        )
                        createRevealAnimation()
                    }
                }
            }
        }
    }

    private fun removeSharedElementListener() {
        (sharedElementEnterTransition as TransitionSet).removeListener(sharedElementListener())
    }

    private fun getHideSplashListener(): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                binding.splash.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                lifecycleScope.launch {
                    delay(100)
                    binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToStart()
                    popDelayed()
                }
            }
        }
    }

    private fun getRevealSplashListener(): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                pokemonDetailViewModel.setRevealAnimationExpandedState(true)
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        }
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

    private fun PokemonDetailFragmentBinding.setPokemonMoves(
        pokemonMoves: Map<String, List<PokemonMove>?>
    ) {
        CoroutineScope(Dispatchers.Default).launch {
//          TODO investigate running this after the animation
            val pokemonMoveList = mutableListOf<PokemonMove>()

            for (moveEntry in pokemonMoves.entries) {
                Log.d("DETAIL", "move key ${moveEntry.key}")
                if (!moveEntry.value.isNullOrEmpty()){
                    pokemonMoveList.addAll(moveEntry.value!!)
                }
                moveEntry.value?.forEach { move ->
                    Log.d("DETAIL", "move key $move")
                }
            }

            withContext(Dispatchers.Main){
                pokemonMovesLoading.visibility = View.GONE

                if (pokemonMoveList.isEmpty()){
                    pokemonMovesEmptyText.visibility = View.VISIBLE
                } else {
                    pokemonMovesEmptyText.visibility = View.GONE
                    pokemonMoveAdapter.submitList(pokemonMoveList)
                }
            }
        }
    }

    private fun setUpPokemonRecyclerView() {
        binding.pokemonMoveRecyclerView.apply {
            adapter = pokemonMoveAdapter
            addRecyclerViewDecoration(this)
            layoutManager = NoScrollLayoutManager(this.context)
        }
    }

    private fun addRecyclerViewDecoration(
        recyclerView: RecyclerView
    ) {
        recyclerView.addItemDecoration(
            PokemonListDecoration(
                recyclerView.context.resources.getDimensionPixelSize(
                    R.dimen.zero
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

}

