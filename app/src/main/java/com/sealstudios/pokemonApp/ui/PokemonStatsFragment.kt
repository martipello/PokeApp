package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sealstudios.pokemonApp.databinding.PokemonStatsFragmentBinding
import com.sealstudios.pokemonApp.ui.viewModel.*

class PokemonStatsFragment : Fragment() {

    private var _binding: PokemonStatsFragmentBinding? = null
    private val binding get() = _binding!!

    private val pokemonStatsViewModel: PokemonStatsViewModel by viewModels({requireParentFragment()})

    private val pokemonBaseStatsViewModel: PokemonBaseStatsViewModel by viewModels()
    private val pokemonWeaknessResistanceViewModel: PokemonWeaknessResistanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = PokemonStatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemonId()
    }

    private fun observePokemonId() {
        pokemonStatsViewModel.pokemonId.observe(viewLifecycleOwner, { id ->
            pokemonBaseStatsViewModel.setPokemonId(id)
            pokemonWeaknessResistanceViewModel.setPokemonId(id)
        })
    }

}