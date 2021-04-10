package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sealstudios.pokemonApp.databinding.StatsFragmentBinding
import com.sealstudios.pokemonApp.ui.viewModel.*

class StatsFragment : Fragment() {

    private var _binding: StatsFragmentBinding? = null
    private val binding get() = _binding!!

    private val statsViewModel: StatsViewModel by viewModels({requireParentFragment()})

    private val baseStatsViewModel: BaseStatsViewModel by viewModels()
    private val weaknessResistanceViewModel: WeaknessResistanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = StatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemonId()
    }

    private fun observePokemonId() {
        statsViewModel.pokemonId.observe(viewLifecycleOwner, { id ->
            baseStatsViewModel.setPokemonId(id)
            weaknessResistanceViewModel.setPokemonId(id)
        })
    }

}