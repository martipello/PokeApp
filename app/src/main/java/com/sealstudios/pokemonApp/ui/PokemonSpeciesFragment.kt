package com.sealstudios.pokemonApp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonSpecies
import com.sealstudios.pokemonApp.databinding.PokemonSpeciesFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.viewModel.PokemonSpeciesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonSpeciesFragment : Fragment() {
    private val pokemonSpeciesViewModel: PokemonSpeciesViewModel by viewModels({ requireParentFragment() })
    private var _binding: PokemonSpeciesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PokemonSpeciesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setLoading()
        observePokemonSpecies()
    }

    private fun observePokemonSpecies() {
        pokemonSpeciesViewModel.pokemonSpecies.observe(viewLifecycleOwner, { pokemonSpecies ->
            Log.d("SPECIES_FRAG", "pokemon species status ${pokemonSpecies.status}")
            when (pokemonSpecies.status) {
                Status.SUCCESS -> {
                    pokemonSpecies.data?.let {
                        binding.setNotEmpty()
                        populatePokemonSpeciesViews(it)
                    }
                    //handle empty
                }
                Status.ERROR -> {
                    binding.setError(pokemonSpecies.message ?: "Oops, something went wrong...")
                    { pokemonSpeciesViewModel.retry() }
                }
                Status.LOADING -> {
                    binding.setLoading()
                }
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun populatePokemonSpeciesViews(pokemonSpecies: PokemonSpecies) =
            lifecycleScope.launch(Dispatchers.Main) {
                setPokemonSpeciesFormData(pokemonSpecies)
            }

    @SuppressLint("DefaultLocale")
    private fun setPokemonSpeciesFormData(
            species: PokemonSpecies
    ) {
        val context = binding.root.context
        binding.pokedexSubtitleText.text = species.pokedex?.capitalize() ?: "N/A"
        species.pokedexEntry?.let {
            binding.pokedexEntryText.visibility = View.VISIBLE
            binding.pokedexEntryText.text = it
        }
        binding.shapeText.text =
                context.getString(R.string.shape_text, species.shape?.capitalize() ?: "N/A")
        binding.formDescriptionText.text =
                context.getString(R.string.form_text, species.formDescription)
        binding.habitatText.text =
                context.getString(R.string.habitat, species.habitat?.capitalize() ?: "N/A")
    }

    private fun PokemonSpeciesFragmentBinding.setLoading() {
        speciesContent.visibility = View.GONE
        speciesError.root.visibility = View.GONE
        speciesLoading.root.visibility = View.VISIBLE
        speciesLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonSpeciesFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        speciesLoading.root.visibility = View.GONE
        speciesContent.visibility = View.GONE
        speciesError.root.visibility = View.VISIBLE
        speciesError.errorImage.visibility = View.GONE
        speciesError.errorText.text = errorMessage
        speciesError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonSpeciesFragmentBinding.setNotEmpty() {
        speciesError.root.visibility = View.GONE
        speciesLoading.root.visibility = View.GONE
        speciesContent.visibility = View.VISIBLE
    }

}
