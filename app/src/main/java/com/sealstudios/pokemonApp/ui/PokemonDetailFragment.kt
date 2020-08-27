package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypes
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.util.PokemonType
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null

    @Inject
    lateinit var glide: RequestManager
    private lateinit var pokemonName: String
    private val binding get() = _binding!!
    private var pokemon: PokemonWithTypes? = null
    private val pokemonDetailViewModel: PokemonDetailViewModel
            by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        val safeArgs: PokemonDetailFragmentArgs by navArgs()
        pokemonName = safeArgs.pokemonName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
//        makeStatusBarTransparent()
        super.onViewCreated(view, savedInstanceState)
        observePokemon()
    }

    private fun setActionBar() {
        val toolbar = binding.toolbar
        val mainActivity = (activity as AppCompatActivity)
        mainActivity.setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(
            mainActivity,
            findNavController(this@PokemonDetailFragment)
        )
        setToolbarTitle()
    }

    private fun setToolbarTitle() {
        binding.toolbar.title = pokemonName.capitalize()
    }

    private fun observePokemon() {
        pokemonDetailViewModel.localPokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            this.pokemon = pokemon
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
                title.text = pokemon.name.capitalize()
                subtitle.text = pokemon.species.capitalize()
                idLabel.text = context.getString(R.string.pokemonId, pokemon.id)
                heightTextView.text = context.getString(R.string.height, pokemon.height)
                weightTextView.text = context.getString(R.string.weight, pokemon.weight)
                movesText.text = "${pokemon.moves}"
                setPokemonTypes(context, it)
                buildPokemonImageView(it)
            }
        }
    }

    private fun buildPokemonImageView(pokemonWithTypes: PokemonWithTypes) {
        glide.asBitmap()
            .load(pokemonWithTypes.pokemon.url)
            .into(binding.pokemonImageView)
    }

    private fun PokemonDetailFragmentBinding.setPokemonTypes(
        context: Context,
        pokemonWithTypes: PokemonWithTypes
    ) {
        pokemonTypesChipGroup.removeAllViews()
        val types =
            PokemonType.getPokemonEnumTypesForPokemonTypes(
                com.sealstudios.pokemonApp.database.`object`.PokemonType.getTypesInOrder(
                    pokemonWithTypes.types
                )
            )
        for (type in types) {
            binding.pokemonTypesChipGroup.addView(
                PokemonType.createPokemonTypeChip(
                    type,
                    context
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}