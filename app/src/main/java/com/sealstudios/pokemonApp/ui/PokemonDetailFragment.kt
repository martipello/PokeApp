package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.google.android.material.tabs.TabLayoutMediator
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.highResPokemonUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithTypes
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonDetailViewPagerAdapter
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.insets.PokemonDetailFragmentInsets
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypesForPokemonTypes
import com.sealstudios.pokemonApp.ui.util.TypesGroupHelper
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonInfoDecorator
import com.sealstudios.pokemonApp.ui.util.dp
import com.sealstudios.pokemonApp.ui.viewModel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.abs

@AndroidEntryPoint
class PokemonDetailFragment : PokemonDetailAnimationManager() {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var pokemonName: String
    private var pokemonId: Int = -1
    private var hasExpanded: Boolean = false

    private val args: PokemonDetailFragmentArgs by navArgs()

    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()
    private val colorViewModel: ColorViewModel by viewModels()

    private val pokemonSpeciesViewModel: PokemonSpeciesViewModel by viewModels()
    private val pokemonInfoViewModel: PokemonInfoViewModel by viewModels()
    private val pokemonStatsViewModel: PokemonStatsViewModel by viewModels()

    private val pokemonMovesViewModel: PokemonMovesViewModel by viewModels()

    private lateinit var viewPagerAdapterAdapter: PokemonDetailViewPagerAdapter
    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        binding.setLoading()
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
            handleAppBarSnapFlag()
            setPokemonImageView(highResPokemonUrl(pokemonId))
            setUpViewPagerAdapter()
            setUpViewPager()
            if (!hasExpanded) {
                handleEnterAnimation()
                pokemonDetailViewModel.setRevealAnimationExpandedState(true)
            }
            observePokemonDetails()
            observePokemonSpecies()
            onFinishedSavingPokemonAbilities()
            onFinishedSavingPokemonBaseStats()
            onFinishedSavingPokemonMoves()
            onFinishedSavingPokemonTypes()
        }
    }

    private fun handleAppBarSnapFlag() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) >= appBarLayout.totalScrollRange - getToolbarHeight()) {
                val toolbarParams = binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
                toolbarParams.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED or SCROLL_FLAG_SNAP
            } else {
                val toolbarParams = binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
                toolbarParams.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
            }
        })
    }

    private fun getToolbarHeight(): Int {
        context?.let { context ->
            return with(TypedValue().also { context.theme.resolveAttribute(android.R.attr.actionBarSize, it, true) }) {
                TypedValue.complexToDimensionPixelSize(this.data, resources.displayMetrics)
            }
        }
        return 0
    }

    private fun setUpViewPagerAdapter() {
        viewPagerAdapterAdapter = PokemonDetailViewPagerAdapter(this)
    }

    private fun setUpViewPager() {
        binding.viewPager.adapter = viewPagerAdapterAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.setPageTransformer(MarginPageTransformer(150))
        binding.viewPager.addItemDecoration(PokemonInfoDecorator(32.dp))
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = "Fragment ${(position + 1)}"
        }.attach()
    }

    private fun setNameAndIDViews(context: Context) {
        binding.title.text = pokemonName.capitalize(Locale.ROOT)
        binding.idLabel.text = context.getString(R.string.pokemonId, pokemonId)
    }

    private fun handleNavigationArgs() {
        pokemonId = PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        pokemonName = args.pokemonName
        binding.pokemonImageViewHolderLayout.pokemonImageDetailViewHolder.transitionName =
                args.transitionName
    }

    private fun setViewModelProperties() {
        setPokemonIdForViewModels(pokemonId)
        colorViewModel.setViewColors(
                args.dominantSwatchRgb,
                args.lightVibrantSwatchRgb
        )
    }

    private fun setPokemonIdForViewModels(pokemonId: Int) {
        pokemonDetailViewModel.setPokemonId(pokemonId)
        pokemonSpeciesViewModel.setPokemonId(pokemonId)
    }

    private fun observePokemonDetails() {
        pokemonDetailViewModel.pokemonDetail.observe(viewLifecycleOwner, { pokemonWithTypes ->
            when (pokemonWithTypes.status) {
                Status.SUCCESS -> {
                    if (pokemonWithTypes.data != null) {
                        populatePokemonDetailViews(pokemonWithTypes.data)
                        binding.setNotEmpty()
                    } else {
                        binding.setError(
                                errorMessage = pokemonWithTypes.message
                                        ?: "Oops, Something went wrong.",
                                fetchPokemon = { setPokemonIdForViewModels(pokemonId) }
                        )
                    }
                }
                Status.ERROR -> {
                    binding.setError(
                            errorMessage = pokemonWithTypes.message
                                    ?: "Oops, Something went wrong.",
                            fetchPokemon = { setPokemonIdForViewModels(pokemonId) }
                    )
                }
                Status.LOADING -> {
                    binding.setLoading()
                }
            }
        })
    }

    private fun onFinishedSavingPokemonAbilities() {
        pokemonDetailViewModel.onFinishedSavingPokemonAbilities.observe(viewLifecycleOwner, {
            pokemonInfoViewModel.setPokemonId(it)
        })
    }

    private fun onFinishedSavingPokemonBaseStats() {
        pokemonDetailViewModel.onFinishedSavingPokemonBaseStats.observe(viewLifecycleOwner, {
            pokemonStatsViewModel.setPokemonId(it)
        })
    }

    private fun onFinishedSavingPokemonMoves() {
        pokemonDetailViewModel.onFinishedSavingPokemonMoves.observe(viewLifecycleOwner, {
            pokemonMovesViewModel.setPokemonId(it)
        })
    }

    private fun onFinishedSavingPokemonTypes() {
        pokemonDetailViewModel.onFinishedSavingPokemonTypes.observe(viewLifecycleOwner, {
//            pokemonWeaknessResistanceViewModel.setPokemonId(it)
        })
    }

    private fun observePokemonSpecies() {
        pokemonSpeciesViewModel.pokemonSpecies.observe(viewLifecycleOwner, { pokemonSpecies ->
            when (pokemonSpecies.status) {
                Status.SUCCESS -> {
                    pokemonSpecies.data?.let {
                        populatePokemonSpeciesViews(it)
                        binding.setSpeciesNotEmpty()
                    }
                }
                else -> {
                    binding.setSpeciesEmpty()
                }
            }
        })
    }

    private fun observeHasExpandedState() {
        pokemonDetailViewModel.revealAnimationExpanded.observe(
                viewLifecycleOwner, { hasExpanded ->
            this.hasExpanded = hasExpanded
            if (hasExpanded) {
                restoreUIState()
            }
        }
        )
    }

    private fun observeUIColor() {
        colorViewModel.dominantAndLightVibrantColors.observe(
                viewLifecycleOwner, { viewColors ->
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
        ContextCompat.getDrawable(binding.root.context, R.drawable.squareangle)?.let {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(it),
                    lightVibrantSwatchRgb
            )
            binding.toolbar.background = it
        }
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
                buildPokemonTypes(pokemon.types)
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

    private fun buildPokemonTypes(
            types: List<PokemonType>
    ) {

        binding.dualTypeChipLayout.typeChip1.pokemonTypeChip.visibility = View.GONE
        binding.dualTypeChipLayout.typeChip2.pokemonTypeChip.visibility = View.GONE
        CoroutineScope(Dispatchers.Default).launch {
            val enumTypes = getPokemonEnumTypesForPokemonTypes(
                    types
            )
            withContext(Dispatchers.Main) {
                TypesGroupHelper(
                        binding.dualTypeChipLayout.pokemonTypesChipGroup,
                        enumTypes
                ).bindChips()
            }
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
        binding.genTextView.text = context.getString(R.string.generation_label)
        binding.genTextView.text = PokemonGeneration.formatGenerationName(
                PokemonGeneration.getGeneration(species.generation ?: "")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun PokemonDetailFragmentBinding.setNotEmpty() {
        mainProgress.root.visibility = View.GONE
        errorLayout.root.visibility = View.GONE
        content.visibility = View.VISIBLE
    }

    private fun PokemonDetailFragmentBinding.setLoading() {
        mainProgress.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
        mainProgress.root.visibility = View.VISIBLE
        content.visibility = View.GONE
        errorLayout.root.visibility = View.GONE
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

    private fun PokemonDetailFragmentBinding.setSpeciesEmpty() {
        subtitle.visibility = View.GONE
        genTextView.visibility = View.GONE
        genTextLabel.visibility = View.GONE
    }

    private fun PokemonDetailFragmentBinding.setSpeciesNotEmpty() {
        subtitle.visibility = View.VISIBLE
        genTextView.visibility = View.VISIBLE
        genTextLabel.visibility = View.VISIBLE
    }

}

