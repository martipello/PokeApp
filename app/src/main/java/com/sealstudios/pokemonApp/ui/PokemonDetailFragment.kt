package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.content.Context
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
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpeciesAndMoves
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.util.addSystemWindowInsetToPadding
import com.sealstudios.pokemonApp.ui.util.alignBelowStatusBar
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.sealstudios.pokemonApp.ui.util.PokemonType as pokemonTypeEnum


@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null

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
        getNavigationArguments()
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

    private fun getNavigationArguments() {
        val safeArgs: PokemonDetailFragmentArgs by navArgs()
        pokemonName = safeArgs.pokemonName
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

    private fun setPokemonImageView(imageName: String) {
        //TODO add a listener that on error tries to display the sprite
        glide.asBitmap()
            .load(imageName)
            .into(binding.pokemonImageView)
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
    ){
        movesHolder.removeAllViews()
        pokemonMoves.forEach {entry ->
            Log.d("PDVM", "key ${entry.key}, value ${entry.value}")
        }
    }

    private fun List<PokemonMove>.getPokemonMoves() : Map<String, List<PokemonMove>?>{
        val moveMap = mutableMapOf<String, MutableList<PokemonMove>?>()
        this.forEach {
            if (it.generation.isNotEmpty()){
                if (moveMap.containsKey(it.generation)){
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

