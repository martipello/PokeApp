package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.highResPokemonUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.insets.PokemonDetailFragmentInsets
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.createPokemonTypeChip
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypesForPokemonTypes
import com.sealstudios.pokemonApp.ui.viewModel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.resume

@AndroidEntryPoint
class PokemonDetailFragment : PokemonDetailAnimationManager() {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String
    private var pokemonId: Int = -1
    private val args: PokemonDetailFragmentArgs by navArgs()
    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()
    private val pokemonSpeciesViewModel: PokemonSpeciesViewModel by viewModels()
    private val pokemonMovesViewModel: PokemonMovesViewModel by viewModels()
    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private var hasExpanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        setPokemonDetailFragmentBinding(binding)
        postponeEnterTransition()
        observeHasExpandedState()
        handleNavigationArgs()
        observeUIColor()
        setViewModelProperties()
        PokemonDetailFragmentInsets().setInsets(binding)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(context = Dispatchers.Main) {
            setNameAndIDViews(view.context)
            setPokemonImageView(highResPokemonUrl(pokemonId))
            if (!hasExpanded) {
                handleEnterAnimation()
                pokemonDetailViewModel.setRevealAnimationExpandedState(true)
            }
            observePokemonDetails()
            observePokemonSpecies()
        }
    }

    private fun setNameAndIDViews(context: Context) {
        binding.title.text = pokemonName.capitalize()
        binding.idLabel.text = context.getString(R.string.pokemonId, pokemonId)
    }

    private fun handleNavigationArgs() {
        pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        pokemonName = args.pokemonName
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.transitionName =
            args.transitionName
    }

    private fun setViewModelProperties() {
        pokemonDetailViewModel.setPokemonId(pokemonId)
        pokemonSpeciesViewModel.setPokemonId(pokemonId)
        pokemonDetailViewModel.setViewColors(
            args.dominantSwatchRgb,
            args.lightVibrantSwatchRgb
        )
    }

    private fun observePokemonDetails() {
        pokemonDetailViewModel.pokemonDetail.observe(viewLifecycleOwner, Observer { pokemonWithTypes ->
            when (pokemonWithTypes.status) {
                Status.SUCCESS -> {
                    pokemonWithTypes.data?.let {
                        populatePokemonDetailViews(it)
                        pokemonMovesViewModel.setPokemon(pokemon = it.pokemon)
                        binding.setNotEmpty()
                    }
                }
                Status.ERROR -> {
                    binding.setError(
                        errorMessage = pokemonWithTypes.message ?: "Oops, Something went wrong.",
                        fetchPokemon = { pokemonDetailViewModel.setPokemonId(pokemonId) }
                    )
                }
                Status.LOADING -> {
                    binding.setLoading()
                }
            }
        })
    }

    private fun observePokemonSpecies() {
        pokemonSpeciesViewModel.pokemonSpecies.observe(viewLifecycleOwner, Observer { pokemonSpecies ->
            when (pokemonSpecies.status) {
                Status.SUCCESS -> {
                    pokemonSpecies.data?.let {
                        populatePokemonSpeciesViews(it)
                    }
                }
                else -> {
                    binding.subtitle.visibility = View.GONE
                    binding.genTextView.visibility = View.GONE
                }
            }
        })
    }

    private fun observeHasExpandedState() {
        pokemonDetailViewModel.revealAnimationExpanded.observe(
            viewLifecycleOwner, Observer { hasExpanded ->
                this.hasExpanded = hasExpanded
                if (hasExpanded) {
                    restoreUIState()
                }
            }
        )
    }

    private fun observeUIColor() {
        pokemonDetailViewModel.dominantAndLightVibrantColors.observe(
            viewLifecycleOwner, Observer { viewColors ->
                setColoredElements(
                    viewColors.dominantColor,
                    viewColors.lightVibrantColor
                )
            })
    }

    private fun setColoredElements(dominantColor: Int, lightVibrantSwatchRgb: Int) {
        if (!hasExpanded) {
            binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.setCardBackgroundColor(
                dominantColor
            )
        }
        binding.splash.setCardBackgroundColor(dominantColor)
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
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder
            .setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    android.R.color.transparent
                )
            )
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

    @SuppressLint("DefaultLocale")
    private fun populatePokemonDetailViews(pokemon: PokemonWithTypes) =
        lifecycleScope.launch(Dispatchers.Main) {
            setPokemonTypes(pokemon.types)
            setPokemonFormData(pokemon)
        }

    @SuppressLint("DefaultLocale")
    private fun populatePokemonSpeciesViews(pokemonSpecies: PokemonSpecies) =
        lifecycleScope.launch(Dispatchers.Main) {
            setPokemonSpeciesFormData(pokemonSpecies)
        }

    private suspend fun setPokemonImageView(imageUrl: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val requestOptions =
                RequestOptions.noTransformation()
            glide.asBitmap()
                .load(imageUrl)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(requestOptions)
                .placeholder(R.drawable.pokeball_vector)
                .addListener(imageRequestListener(continuation))
                .into(binding.pokemonImageViewHolderLayout.pokemonImageView)
        }

    private fun imageRequestListener(continuation: CancellableContinuation<Boolean>): RequestListener<Bitmap?> {
        return object : RequestListener<Bitmap?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Bitmap?>,
                isFirstResource: Boolean
            ): Boolean {
                if (continuation.isActive) continuation.resume(false)
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                if (continuation.isActive) continuation.resume(true)
                return false
            }
        }
    }


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
        pokemonWithTypes: PokemonWithTypes
    ) {
        val context = binding.root.context
        binding.title.text = pokemonWithTypes.pokemon.name.capitalize()
        binding.idLabel.text = context.getString(R.string.pokemonId, pokemonWithTypes.pokemon.id)
        val doubleHeight: Double = pokemonWithTypes.pokemon.height.toDouble()
        val doubleWeight: Double = pokemonWithTypes.pokemon.weight.toDouble()
        val height = doubleHeight / 10
        val weight = doubleWeight / 10
        binding.heightTextView.text = context.getString(R.string.height, height)
        binding.weightTextView.text = context.getString(R.string.weight, weight)
    }

    @SuppressLint("DefaultLocale")
    private fun setPokemonSpeciesFormData(
        species: PokemonSpecies
    ) {
        val context = binding.root.context
        binding.subtitle.text = species.species.capitalize()
        binding.genTextView.text = context.getString(
            R.string.generation, PokemonGeneration.formatGenerationName(
                PokemonGeneration.getGeneration(species.generation ?: "")
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun PokemonDetailFragmentBinding.setLoading() {
        content.visibility = View.GONE
        errorLayout.root.visibility = View.GONE
        mainProgress.root.visibility = View.VISIBLE
        mainProgress.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonDetailFragmentBinding.setError(errorMessage: String, fetchPokemon: () -> Unit) {
        mainProgress.root.visibility = View.GONE
        content.visibility = View.GONE
        errorLayout.root.visibility = View.VISIBLE
        errorLayout.errorImage.visibility = View.VISIBLE
        errorLayout.errorText.text = errorMessage
        errorLayout.retryButton.setOnClickListener {
            fetchPokemon()
        }
    }

    private fun PokemonDetailFragmentBinding.setNotEmpty() {
        mainProgress.root.visibility = View.GONE
        errorLayout.root.visibility = View.GONE
        content.visibility = View.VISIBLE
    }

}

