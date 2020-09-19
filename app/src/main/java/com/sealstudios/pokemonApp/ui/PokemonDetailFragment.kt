package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
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
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
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
    private var transitionListenerAdapter : TransitionListenerAdapter? = null
    private var hasExpanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    private fun transitionListenerAdapter(): TransitionListenerAdapter {
        return transitionListenerAdapter ?: object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                super.onTransitionStart(transition)
                Log.d("DETAIL", "onTransitionStart")
                _binding?.let {
                    Log.d("DETAIL", "binding not null")
                    //TODO resize image view
                    if (hasExpanded) {
                        Log.d("DETAIL", "view is expanded")
                        createHideAnimation()
                    }
                }
            }
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                _binding?.let {
                    //TODO resize image view
                    if (!hasExpanded) {
                        pokemonDetailViewModel.setRevealAnimationExpandedState(true)
                        hasExpanded = true
                        createRevealAnimation()
                    }
                }
            }
        }
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
        val pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        pokemonDetailViewModel.setId(pokemonId)
        val imageUrl = highResPokemonUrl(pokemonId)
        setPokemonImageView(imageUrl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        observeHasExpandedState()
        observePokemon()
    }

    @SuppressLint("DefaultLocale")
    private fun setActionBar() {
        binding.toolbar.outlineProvider = null
        binding.appBarLayout.outlineProvider = null
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar.apply {
            this?.setHomeButtonEnabled(true)
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
                resource?.let { bitmap ->
                    val builder = Palette.Builder(bitmap)
                    builder.generate { palette: Palette? ->
                        palette?.dominantSwatch?.rgb?.let {
//                            binding.pokemonImageViewHolder.apply {
//                                strokeColor = it
//                            }
                        }
                        palette?.lightVibrantSwatch?.rgb?.let {
                            binding.detailRoot.apply {
//                                this.setBackground(it)
                            }
                        }
                    }
                }
                startPostponedEnterTransition()
                return false
            }
        }
    }

    private fun createRevealAnimation() {
        val x: Int = binding.splash.right / 2
        val y: Int = binding.splash.top + binding.splash.bottom / 10
        val endRadius =
            hypot(binding.splash.width.toDouble(), binding.splash.height.toDouble()).toInt()
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
        val y: Int = binding.splash.top + binding.splash.bottom / 10
        val endRadius =
            hypot(binding.splash.width.toDouble(), binding.splash.height.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            binding.splash, x, y,
            endRadius.toFloat(),
            0f
        )
        binding.splash.visibility = View.INVISIBLE
        anim.duration = 500
        anim.start()
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

}

