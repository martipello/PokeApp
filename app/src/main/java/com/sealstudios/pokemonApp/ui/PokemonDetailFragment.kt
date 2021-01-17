package com.sealstudios.pokemonApp.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
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
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.insets.PokemonDetailFragmentInsets
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitTransitionEnd
import com.sealstudios.pokemonApp.ui.listenerExtensions.startAndWait
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.createPokemonTypeChip
import com.sealstudios.pokemonApp.ui.util.PokemonType.Companion.getPokemonEnumTypesForPokemonTypes
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonMovesViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.resume

@AndroidEntryPoint
class PokemonDetailFragment : PokemonDetailAnimationManager(){

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String
    private var pokemonId: Int = -1
    private val args: PokemonDetailFragmentArgs by navArgs()
    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()
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
        pokemonDetailViewModel.setViewColors(
            args.dominantSwatchRgb,
            args.lightVibrantSwatchRgb
        )
    }

//    private fun observePokemon() {
//        pokemonDetailViewModel.pokemon.observe(viewLifecycleOwner, Observer { resourceWithPokemon ->
//            lifecycleScope.launch {
//                when(resourceWithPokemon.status){
//                    Status.SUCCESS -> {
//                        populateViews(resourceWithPokemon.data)
//                        setDataState()
//                    }
//                    Status.ERROR -> {
//
//                    }
//                    Status.LOADING -> {
//
//                    }
//                }
//            }
//        })
//    }

    private fun observePokemonDetails() {
        pokemonDetailViewModel.pokemonDetail.observe(viewLifecycleOwner, Observer { resourceWithPokemon ->
            Log.d("PDVM", "observe pokemonDetail status ${resourceWithPokemon.status}")
            when(resourceWithPokemon.status){
                Status.SUCCESS -> {
                    resourceWithPokemon.data?.let {
                        populateViews(it)
                        setDataState()
                    }
                    //handle empty
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

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
    private fun populateViews(pokemon: PokemonWithTypes) =
        lifecycleScope.launch(Dispatchers.Main) {
            pokemon?.let {
                setPokemonTypes(it.types)
                setPokemonFormData(it)
            }
        }

    private fun setLoadingState() {
        binding.mainProgress.visibility = View.VISIBLE
        binding.content.visibility = View.GONE
    }

    private fun setErrorState() {
        binding.mainProgress.visibility = View.GONE
        binding.content.visibility = View.GONE
    }

    private fun setDataState() {
        binding.mainProgress.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
    }

    private fun setDataEmptyState() {
        binding.mainProgress.visibility = View.GONE
        binding.content.visibility = View.GONE
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
                continuation.resume(false)
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any,
                target: Target<Bitmap?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                continuation.resume(true)
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
        it: PokemonWithTypes
    ) {
        val context = binding.root.context
        binding.title.text = it.pokemon.name.capitalize()
        binding.idLabel.text = context.getString(R.string.pokemonId, it.pokemon.id)
//        binding.subtitle.text = it.species?.species?.capitalize()
//        binding.genTextView.text = context.getString(R.string.generation, PokemonGeneration.formatGenerationName(
//            PokemonGeneration.getGeneration(it.species?.generation ?: "")))
        val doubleHeight: Double = it.pokemon.height.toDouble()
        val doubleWeight: Double = it.pokemon.weight.toDouble()
        val height = doubleHeight / 10
        val weight = doubleWeight / 10
        binding.heightTextView.text = context.getString(R.string.height, height)
        binding.weightTextView.text = context.getString(R.string.weight, weight)
//        binding.pokedexSubtitle.text =
//            context.getString(R.string.pok_dex_gen, it.species?.pokedex?.capitalize() ?: "N/A")
//        it.species?.pokedexEntry?.let {
//            binding.pokedexEntryText.visibility = View.VISIBLE
//            binding.pokedexEntryText.text = it
//        }
//        binding.shapeText.text =
//            context.getString(R.string.shape_text, it.species?.shape?.capitalize() ?: "N/A")
//        binding.formDescriptionText.text =
//            context.getString(R.string.form_text, it.species?.formDescription ?: "N/A")
//        binding.habitatText.text =
//            context.getString(R.string.habitat, it.species?.habitat?.capitalize() ?: "N/A")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

