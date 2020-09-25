package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.highResPokemonUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves.Companion.getPokemonMoves
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.util.addSystemWindowInsetToPadding
import com.sealstudios.pokemonApp.ui.util.alignBelowStatusBar
import com.sealstudios.pokemonApp.ui.util.dp
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.hypot
import com.sealstudios.pokemonApp.ui.util.PokemonType as pokemonTypeEnum

@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String

    private val args: PokemonDetailFragmentArgs by navArgs()
    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()

    private var pokemon: PokemonWithTypesAndSpeciesAndMoves? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        setAndPostponeEnterAnimation()
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        setInsets()
        handleNavigationArgs()
        observeHasExpandedState()
        observeUIColor()
        return binding.root
    }

    private fun setAndPostponeEnterAnimation() {
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
        (sharedElementEnterTransition as TransitionSet).addListener(transitionListenerAdapter())
    }

    private fun setInsets() {
        binding.appBarLayout.alignBelowStatusBar()
        binding.toolbar.addSystemWindowInsetToPadding(false)
        binding.toolbarLayout.addSystemWindowInsetToPadding(false)
        binding.detailRoot.addSystemWindowInsetToPadding(top = false)
        binding.scrollView.addSystemWindowInsetToPadding(bottom = true)
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

    private fun setColoredElements(dominantColor: Int, lightVibrantSwatchRgb: Int) {
        binding.splash.setBackgroundColor(dominantColor)
        binding.squareangleMask.setColorFilter(lightVibrantSwatchRgb)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.setCardBackgroundColor(
            dominantColor
        )
    }

    private fun setViewModelProperties(pokemonId: Int) {
        pokemonDetailViewModel.setId(pokemonId)
        pokemonDetailViewModel.setViewColors(
            args.dominantSwatchRgb,
            args.lightVibrantSwatchRgb
        )
    }

    private fun startExitTransition() {
        //TODO try to make the circle view appear after the hide reveal finishes
        createHideAnimation()
        binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToStart()
    }

    private fun transitionListenerAdapter(): TransitionListenerAdapter {
        return transitionListenerAdapter ?: object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                _binding?.let {
                    if (!hasExpanded) {
                        it.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToEnd()
                        createRevealAnimation()
                    }
                }
            }
        }
    }

    private fun removeSharedElementListener() {
        (sharedElementEnterTransition as TransitionSet).removeListener(transitionListenerAdapter())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        observePokemon()
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

    private fun observePokemon() {
        pokemonDetailViewModel.pokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            this.pokemon = pokemon
            pokemon?.let {
                populateViews()
            }
        })
    }

    private fun observeHasExpandedState() {
        pokemonDetailViewModel.revealAnimationExpanded.observe(
            viewLifecycleOwner,
            Observer { hasExpanded ->
                Log.d("DETAIL", "hasExpanded $hasExpanded")
                this.hasExpanded = hasExpanded
            })
    }

    private fun observeUIColor() {
        pokemonDetailViewModel.dominantAndLightVibrantColors.observe(
            viewLifecycleOwner,
            Observer { viewColors ->
                Log.d("DETAIL", "viewColors $viewColors")
                setColoredElements(
                    viewColors.dominantColor,
                    viewColors.lightVibrantColor
                )
                if (hasExpanded){
                    restoreUIState()
                }
            })
    }

    private fun restoreUIState() {
        binding.splash.visibility = View.VISIBLE
        binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToEnd()
        binding.root.post {
            createRevealAnimation()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun populateViews() {
        pokemon?.let {
            with(binding) {
                setPokemonTypes(it.types)
                setPokemonFormData(it)
                setPokemonMoves(it.moves.getPokemonMoves())
            }
        }
    }

    private fun setPokemonImageView(imageUrl: String) {
        glide.asBitmap()
            .load(imageUrl)
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
                    .load(pokemon?.pokemon?.sprite)
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
        anim.addListener(getRevealAnimatorListener())
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

        anim.addListener(getHideAnimatorListener())
        anim.start()
    }

    private fun getHideAnimatorListener(): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                binding.splash.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                popDelayed()
            }
        }
    }

    private fun getRevealAnimatorListener(): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                pokemonDetailViewModel.setRevealAnimationExpandedState(true)
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        }
    }

    private fun popDelayed() {
        lifecycleScope.launch {
            delay(100)
            findNavController().popBackStack()
        }
    }

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

    private fun PokemonDetailFragmentBinding.setPokemonTypes(
        pokemonTypes: List<PokemonType>
    ) {
        pokemonTypesChipGroup.removeAllViews()
        val types =
            pokemonTypeEnum.getPokemonEnumTypesForPokemonTypes(
                PokemonType.getTypesInOrder(types = pokemonTypes)
            )
        for (type in types) {
            pokemonTypesChipGroup.addView(
                pokemonTypeEnum.createPokemonTypeChip(type, binding.root.context)
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
        movesHolder.removeAllViews()
        pokemonMoves.forEach { entry ->
            Log.d("PDVM", "key ${entry.key}, value ${entry.value}")
        }
    }

    /***
     * driving this state are motion layout animation and animation utils createRevealAnimation
     */
    /***
     * hasTransitionedIn - should tell the view if the image is large (motion layout)
     * if the splash screen is visible (createRevealAnimation), the color of the splash screen,
     * and the app bar tint
     * TODO: create a view model for this state
     * */

}

