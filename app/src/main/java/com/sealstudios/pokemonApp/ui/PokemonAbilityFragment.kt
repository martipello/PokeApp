package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonAbility
import com.sealstudios.pokemonApp.databinding.PokemonAbilityFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration.Companion.getGeneration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonAbilityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonAbilityFragment : Fragment() {
    private val pokemonAbilityViewModel: PokemonAbilityViewModel by viewModels({ requireParentFragment() })
    private var _binding: PokemonAbilityFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PokemonAbilityFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        observePokemonAbility()
        return binding.root
    }


    private fun observePokemonAbility() {
//        pokemonAbilityViewModel.pokemonAbility.observe(viewLifecycleOwner, Observer { pokemonSpecies ->
//            when (pokemonSpecies.status) {
//                Status.SUCCESS -> {
//                    pokemonSpecies.data?.let {
//                        binding.setNotEmpty()
//                        populatePokemonSpeciesViews(it)
//                    }
//                    //handle empty
//                }
//                Status.ERROR -> {
//                    Log.d("PAF", "ERROR CODE ${pokemonSpecies.code} ERROR MESSAGE ${pokemonSpecies.message}")
//                    if (pokemonSpecies.code == ErrorCodes.NOT_FOUND.code){
//                        binding.setEmpty()
//                    } else {
//                        binding.setError(pokemonSpecies.message ?: "Oops, something went wrong...")
//                        { pokemonAbilityViewModel.retry() }
//                    }
//                }
//                Status.LOADING -> {
//                    binding.setLoading()
//                }
//            }
//        })
    }

    @SuppressLint("DefaultLocale")
    private fun populatePokemonSpeciesViews(pokemonAbility: PokemonAbility) =
        lifecycleScope.launch(Dispatchers.Main) {
            setPokemonAbilityFormData(pokemonAbility)
        }

    @SuppressLint("DefaultLocale")
    private fun setPokemonAbilityFormData(
        pokemonAbility: PokemonAbility
    ) {
        binding.nameText.text = pokemonAbility.name
        binding.flavorTextText.text = pokemonAbility.flavorText
        binding.generationText.text = getGeneration(pokemonAbility.generation).name
        binding.versionGroupText.text = pokemonAbility.abilityEffectChangeVersionGroup
        binding.shortEffectText.text = pokemonAbility.abilityEffectEntryShortEffect
        binding.effectText.text = pokemonAbility.abilityEffectEntry
    }

    private fun PokemonAbilityFragmentBinding.setLoading() {
        abilityContent.visibility = View.GONE
        abilityError.root.visibility = View.GONE
        abilityLoading.root.visibility = View.VISIBLE
        abilityLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonAbilityFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        abilityLoading.root.visibility = View.GONE
        abilityContent.visibility = View.GONE
        abilityError.root.visibility = View.VISIBLE
        abilityError.errorImage.visibility = View.GONE
        abilityError.errorText.text = errorMessage
        abilityError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonAbilityFragmentBinding.setNotEmpty() {
        abilityError.root.visibility = View.GONE
        abilityLoading.root.visibility = View.GONE
        abilityContent.visibility = View.VISIBLE
    }

    private fun PokemonAbilityFragmentBinding.setEmpty() {
        root.visibility = View.GONE
    }

}
