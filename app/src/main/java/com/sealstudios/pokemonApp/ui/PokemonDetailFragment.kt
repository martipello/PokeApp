package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.sealstudios.pokemonApp.MainActivity
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private var pokemon: Pokemon? = null
    private val pokemonDetailViewModel: PokemonDetailViewModel
            by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemon()
        pokemon?.name?.let { setActionBarTitle(it) }
    }

    private fun setActionBarTitle(title: String) {
        activity?.actionBar?.title = title.capitalize(Locale.ROOT)
        (activity as MainActivity).supportActionBar?.title = title.capitalize(Locale.ROOT)
    }

    private fun observePokemon() {
        pokemonDetailViewModel.localPokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            this.pokemon = pokemon
            pokemon?.let {
                populateViews()
                //TODO check something more meaningful to decide if we need to search the pokeapi for more information
                if (pokemon.weight < 1) {
                    pokemonDetailViewModel.getRemotePokemon(pokemon)
                }
            }
        })
    }

    private fun populateViews() {
        pokemon?.let {
            setActionBarTitle(it.name)
            binding.nameTextView.text = it.name
            binding.weightTextView.text = "${it.weight}"
            binding.heightTextView.text = "${it.height}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}