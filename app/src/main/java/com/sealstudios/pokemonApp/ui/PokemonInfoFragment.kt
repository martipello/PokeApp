package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sealstudios.pokemonApp.databinding.PokemonInfoFragmentBinding
import com.sealstudios.pokemonApp.ui.viewModel.PokemonAbilityViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonInfoViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonSpeciesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonInfoFragment : Fragment() {

    private var _binding: PokemonInfoFragmentBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: PokemonInfoViewModel by viewModels({ requireParentFragment() })
    private val speciesViewModel: PokemonSpeciesViewModel by viewModels()
    private val abilityViewModel: PokemonAbilityViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = PokemonInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemonId()
    }

    private fun observePokemonId() {
        infoViewModel.pokemonId.observe(viewLifecycleOwner, { id ->
            speciesViewModel.setPokemonId(id)
            abilityViewModel.setPokemonId(id)
        })
    }
}