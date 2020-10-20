package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
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
import androidx.core.animation.doOnEnd
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
import com.bumptech.glide.request.target.CustomTarget
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
import kotlinx.coroutines.android.awaitFrame
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

    private fun addBackButtonCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleExitTransition()
            this.remove()
        }
    }

    private fun handleExitTransition() {
        viewLifecycleOwner.lifecycleScope.launch {

            val circleHideAnimationAsync = async {

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
            circleHideAnimationAsync.await()

        }
    }

    private suspend fun popDelayed() {
        withContext(Dispatchers.Main) {
            delay(100)
            findNavController().popBackStack()
        }
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
        observeHasExpandedState()
        observePokemon()
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
        }

//            Log.d("DETAIL","after startPostponedEnterTransition")
//            observePokemon()
//            setViewModelProperties(pokemonId)
//            setUpPokemonAdapter()
//            setUpPokemonMovesRecyclerView()
//            observeHasExpandedState()
//            observeUIColor()


    }

    private suspend fun handleEnterAnimation() {
        viewLifecycleOwner.lifecycleScope.launch {

            val sharedElementEnterTransitionAsync = async {
                sharedElementEnterTransition = TransitionInflater.from(context)
                    .inflateTransition(R.transition.shared_element_transition)
                (sharedElementEnterTransition as TransitionSet).run {
                    awaitTransitionEnd {
                        startPostponedEnterTransition()
                    }
                }
            }
            sharedElementEnterTransitionAsync.await()

            binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToState(
                R.id.large_image
            )

            val x = binding.splash.right / 2
            val location = IntArray(2)
            binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView
                .getLocationOnScreen(location)
            val y = location[1] + binding.pokemonImageViewHolderLayout
                .pokemonBackgroundCircleView.height / 2
            val splashRevealAnimator = async {
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
            }
            splashRevealAnimator.await()

            pokemonDetailViewModel.setRevealAnimationExpandedState(true)

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
//        transitionPokemonImageFromSmallToLarge(binding)
//        binding.root.post {
//            createRevealAnimation()
//        }
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
            viewLifecycleOwner.lifecycleScope.launch {
                setPokemonTypes(it.types)
                setPokemonFormData(it)
//                setPokemonMoves(it.moves.getPokemonMoves())
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

    // ------------ populate Pokemon data views ------------ //

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleExitTransition()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int, pokemonMove: PokemonMove) {
        TODO("Not yet implemented")
    }

}

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { continuation ->

    continuation.invokeOnCancellation { cancel() }

    this.addListener(object : AnimatorListenerAdapter() {
        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator) {

            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator) {

            animation.removeListener(this)

            if (continuation.isActive) {
                // If the coroutine is still active...
                if (endedSuccessfully) {
                    // ...and the Animator ended successfully, resume the coroutine
                    continuation.resume(Unit)
                } else {
                    // ...and the Animator was cancelled, cancel the coroutine too
                    continuation.cancel()
                }
            }
        }
    })

}

suspend fun TransitionSet.awaitTransitionEnd(executeBeforeReturn: (CancellableContinuation<Unit>) -> Unit) =
    suspendCancellableCoroutine<Unit> { continuation ->

        val listener = object : TransitionListenerAdapter() {
            private var endedSuccessfully = true

            override fun onTransitionCancel(transition: Transition) {
                super.onTransitionCancel(transition)
                endedSuccessfully = false
            }

            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                transition.removeListener(this)

                if (continuation.isActive) {
                    // If the coroutine is still active...
                    if (endedSuccessfully) {
                        // ...and the Animator ended successfully, resume the coroutine
                        continuation.resume(Unit)
                    } else {
                        // ...and the Animator was cancelled, cancel the coroutine too
                        continuation.cancel()
                    }
                }
            }
        }
        continuation.invokeOnCancellation { removeListener(listener) }
        this.addListener(listener)
        executeBeforeReturn(continuation)
    }


suspend fun Animator.startAndWait() = suspendCancellableCoroutine<Unit> { continuation ->
    continuation.invokeOnCancellation { cancel() }

    this.addListener(object : AnimatorListenerAdapter() {

        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator?) {
            endedSuccessfully = false
        }

        override fun onAnimationStart(animation: Animator?) {

            animation?.removeListener(this)

            if (continuation.isActive) {
                // If the coroutine is still active...
                if (endedSuccessfully) {
                    // ...and the Animator ended successfully, resume the coroutine
                    continuation.resume(Unit)
                } else {
                    // ...and the Animator was cancelled, cancel the coroutine too
                    continuation.cancel()
                }
            }
        }
    })

    start()

}





