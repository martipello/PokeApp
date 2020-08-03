package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding

class PokemonDetailFragment : Fragment() {

    private var _binding: PokemonDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSecondButton()
    }

    private fun setSecondButton() {
        binding.buttonSecond.setOnClickListener {
            navigateToFirstFragment()
        }
    }

    private fun navigateToFirstFragment() {
        NavHostFragment.findNavController(this@PokemonDetailFragment)
                .navigate(R.id.action_SecondFragment_to_FirstFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}