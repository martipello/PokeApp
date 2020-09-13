package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import androidx.palette.graphics.Palette
import androidx.transition.TransitionInflater
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.Pokemon.Companion.highResPokemonUrl
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonViewHolder
import com.sealstudios.pokemonApp.ui.util.addSystemWindowInsetToPadding
import com.sealstudios.pokemonApp.ui.util.alignBelowStatusBar
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.sealstudios.pokemonApp.ui.util.PokemonType as pokemonTypeEnum


@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null
    private val args: PokemonDetailFragmentArgs by navArgs()

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String
    private val binding get() = _binding!!
    private var pokemon: PokemonWithTypesAndSpeciesAndMoves? = null
    private val pokemonDetailViewModel: PokemonDetailViewModel
            by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        setInsets()
        pokemonName = args.pokemonName
        binding.pokemonImageViewHolder.transitionName = args.transitionName
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
        postponeEnterTransition()
        val imageUrl = highResPokemonUrl(
            PokemonViewHolder.pokemonIdFromTransitionName(args.transitionName).toInt()
        )
        setPokemonImageView(imageUrl)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        super.onViewCreated(view, savedInstanceState)
        observePokemon()
    }

    private fun setInsets() {
        binding.appBarLayout.alignBelowStatusBar()
        binding.toolbar.addSystemWindowInsetToPadding(false)
        binding.toolbarLayout.addSystemWindowInsetToPadding(false)
        binding.detailRoot.addSystemWindowInsetToPadding(top = false)
        binding.scrollView.addSystemWindowInsetToPadding(bottom = true)
    }

    @SuppressLint("DefaultLocale")
    private fun setActionBar() {
        val toolbar = binding.toolbar
        val mainActivity = (activity as AppCompatActivity)
        toolbar.outlineProvider = null
        binding.appBarLayout.outlineProvider = null
        mainActivity.setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(
            mainActivity,
            findNavController(this@PokemonDetailFragment)
        )
        toolbar.title = pokemonName.capitalize()
    }

    private fun observePokemon() {
        pokemonDetailViewModel.pokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            this.pokemon = pokemon
            Log.d("MOVES", pokemon.moves.toString())
            pokemon?.let {
                populateViews()
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun populateViews() {
        val context = binding.title.context
        pokemon?.let {
            with(binding) {
                val pokemon = it.pokemon
                val moves = it.moves.getPokemonMoves()
                setPokemonImageView(it.pokemon.image)
                setPokemonTypes(context, it.types)
                setPokemonFormData(pokemon, it, context)
                setPokemonMoves(context = context, pokemonMoves = moves)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun PokemonDetailFragmentBinding.setPokemonFormData(
        pokemon: Pokemon,
        it: PokemonWithTypesAndSpeciesAndMoves,
        context: Context
    ) {
        title.text = pokemon.name.capitalize()
        subtitle.text = it.species.species.capitalize()
        genTextView.text = context.getString(R.string.generation, it.species.generation)
        idLabel.text = context.getString(R.string.pokemonId, pokemon.id)
        heightTextView.text = context.getString(R.string.height, pokemon.height)
        weightTextView.text = context.getString(R.string.weight, pokemon.weight)
        pokedexSubtitle.text =
            context.getString(R.string.pok_dex_gen, it.species.pokedex?.capitalize())
        pokedexEntryText.text = it.species.pokedexEntry
        shapeText.text = context.getString(R.string.shape_text, it.species.shape?.capitalize())
        formDescriptionText.text = context.getString(R.string.form_text, it.species.formDescription)
        habitatText.text = context.getString(R.string.habitat, it.species.habitat?.capitalize())
    }

    private fun setPokemonImageView(imageUrl: String) {
        glide.asBitmap()
            .load(imageUrl)
            .addListener(createRequestListener())
            .into(binding.pokemonImageView)
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
                    .into(binding.pokemonImageView)
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
//                resource?.let { bitmap ->
//                    val builder = Palette.Builder(bitmap)
//                    builder.generate { palette: Palette? ->
//                        palette?.dominantSwatch?.rgb?.let {
//                            binding.pokemonImageViewHolder.apply {
//                                strokeColor = it
//                            }
//                        }
//                        palette?.lightVibrantSwatch?.rgb?.let {
//                            binding.pokemonImageViewHolder.apply {
////                                setCardBackgroundColor(it)
//                            }
//                        }
//                    }
//                }
                startPostponedEnterTransition()
                return false
            }
        }
    }

    private fun PokemonDetailFragmentBinding.setPokemonTypes(
        context: Context,
        pokemonTypes: List<PokemonType>
    ) {
        pokemonTypesChipGroup.removeAllViews()
        val types =
            pokemonTypeEnum.getPokemonEnumTypesForPokemonTypes(
                PokemonType.getTypesInOrder(types = pokemonTypes)
            )
        for (type in types) {
            pokemonTypesChipGroup.addView(
                pokemonTypeEnum.createPokemonTypeChip(type, context)
            )
        }
    }

    private fun PokemonDetailFragmentBinding.setPokemonMoves(
        context: Context,
        pokemonMoves: Map<String, List<PokemonMove>?>
    ) {
        movesHolder.removeAllViews()
        pokemonMoves.forEach { entry ->
            Log.d("PDVM", "key ${entry.key}, value ${entry.value}")
        }
    }

    private fun List<PokemonMove>.getPokemonMoves(): Map<String, List<PokemonMove>?> {
        val moveMap = mutableMapOf<String, MutableList<PokemonMove>?>()
        this.forEach {
            if (it.generation.isNotEmpty()) {
                if (moveMap.containsKey(it.generation)) {
                    val list = moveMap[it.generation]
                    list?.add(it)
                    moveMap[it.generation] = list
                } else {
                    moveMap[it.generation] = mutableListOf(it)
                }
            }
        }
        return moveMap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

