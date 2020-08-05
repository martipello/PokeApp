package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val pokemonDetailViewModel: PokemonDetailViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setActionBarTitle(title: String){
        activity?.actionBar?.title = title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemonList()
        setSecondButton()
    }


    private fun observePokemonList() {
        pokemonDetailViewModel.localPokemon.observe(viewLifecycleOwner, Observer { pokemonList ->
            pokemonList?.let { populateViews(it) }
        })
    }

    private fun populateViews(pokemon: Pokemon){
        setActionBarTitle(pokemon.name)
        binding.textviewSecond.text = pokemon.name
    }

    private fun setSecondButton() {
        binding.buttonSecond.setOnClickListener {
            navigateToFirstFragment()
        }
    }

    private fun navigateToFirstFragment() {
        NavHostFragment.findNavController(this@PokemonDetailFragment).popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}