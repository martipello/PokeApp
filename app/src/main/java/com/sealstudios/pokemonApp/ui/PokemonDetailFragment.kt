package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
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

    @SuppressLint("DefaultLocale")
    private fun setActionBarTitle(title: String) {
        activity?.actionBar?.title = title.capitalize()
        (activity as MainActivity).supportActionBar?.title = title.capitalize()
    }

    private fun observePokemon() {
        pokemonDetailViewModel.localPokemon.observe(viewLifecycleOwner, Observer { pokemon ->
            this.pokemon = pokemon
            pokemon?.let {
                populateViews()
            }
        })
    }

    private fun populateViews() {
        pokemon?.let {
            with(binding) {
                setActionBarTitle(it.name)
                nameTextView.text = it.name
                weightTextView.text = "${it.weight}"
                heightTextView.text = "${it.height}"
                movesTextView.text = "${it.moves}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}