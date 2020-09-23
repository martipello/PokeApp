package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.hypot
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
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
    private var transitionListenerAdapter : TransitionListenerAdapter? = null
    private var hasExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            startExitTransition()
            this.remove()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true);
        setAndPostponeEnterAnimation()
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        setInsets()
        handleNavigationArgs()
        return binding.root
    }

    private fun setAndPostponeEnterAnimation() {
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
        (sharedElementEnterTransition as TransitionSet).addListener(transitionListenerAdapter())
    }

    private fun startExitTransition() {
        binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToStart();
        //TODO try to make the circle view appear after the hide reveal finishes
        createHideAnimation()
    }

    private fun transitionListenerAdapter(): TransitionListenerAdapter {
        return transitionListenerAdapter ?: object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                _binding?.let {
                    if (!hasExpanded) {
                        pokemonDetailViewModel.setRevealAnimationExpandedState(true)
                        it.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.transitionToEnd();
                        createRevealAnimation()
                    }
                }
            }
        }
    }

    private fun setMotionLayoutListener() {
        binding.pokemonImageViewHolderLayout.pokemonImageViewSizeHolder.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (hasExpanded){
                    findNavController().popBackStack()
                }
            }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    private fun removeSharedElementListener() {
        (sharedElementEnterTransition as TransitionSet).removeListener(transitionListenerAdapter())
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
        binding.pokemonImageViewHolderLayout.pokemonImageViewHolder.transitionName = args.transitionName
        binding.splash.setBackgroundColor(args.dominantSwatchRgb)
        binding.pokemonImageViewHolderLayout.pokemonBackgroundCircleView.setCardBackgroundColor(args.dominantSwatchRgb)
        binding.squareangleMask.setColorFilter(args.lightVibrantSwatchRgb)
        val pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        pokemonDetailViewModel.setId(pokemonId)
        val imageUrl = highResPokemonUrl(pokemonId)
        setPokemonImageView(imageUrl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        setMotionLayoutListener()
        observeHasExpandedState()
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
        pokemonDetailViewModel.revealAnimationExpanded.observe(viewLifecycleOwner, Observer { hasExpanded ->
            hasExpanded?.let {
                this.hasExpanded = hasExpanded
            }
        })
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
        val y = binding.splash.top + (binding.splash.bottom / 10) * 3

        val endRadius =
            hypot(binding.splash.width.toDouble(),
                binding.splash.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.splash, x, y,
            0f,
            endRadius.toFloat()
        )

        binding.splash.visibility = View.VISIBLE
        anim.start()
    }

    private fun createHideAnimation() {
        val x: Int = binding.splash.right / 2
        val y: Int = binding.splash.top + (binding.splash.bottom / 10) * 3

        val startRadius =
            hypot(binding.splash.width.toDouble(),
                binding.splash.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.splash, x, y,
            startRadius.toFloat(),
            90.dp.toFloat()
        )

        anim.addListener(getHideRevealAnimatorListener())
        anim.start()
    }

    private fun getHideRevealAnimatorListener(): Animator.AnimatorListener {
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

