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
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Species
import com.sealstudios.pokemonApp.databinding.SpeciesFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.viewModel.SpeciesViewModel
import com.sealstudios.pokemonApp.util.extensions.capitalize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpeciesFragment : Fragment() {
    private val speciesViewModel: SpeciesViewModel by viewModels({ requireParentFragment() })
    private var _binding: SpeciesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SpeciesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemonSpecies()
    }

    private fun observePokemonSpecies() {
        speciesViewModel.species.observe(viewLifecycleOwner, { pokemonSpecies ->
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
                    { speciesViewModel.retry() }
                }
                Status.LOADING -> {
                    binding.setLoading()
                }
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun populatePokemonSpeciesViews(species: Species) =
            lifecycleScope.launch(Dispatchers.Main) {
                setPokemonSpeciesFormData(species)
            }

    @SuppressLint("DefaultLocale")
    private fun setPokemonSpeciesFormData(
            species: Species
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

    private fun SpeciesFragmentBinding.setLoading() {
        speciesContent.visibility = View.GONE
        speciesError.root.visibility = View.GONE
        speciesLoading.root.visibility = View.VISIBLE
        speciesLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun SpeciesFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        speciesLoading.root.visibility = View.GONE
        speciesContent.visibility = View.GONE
        speciesError.root.visibility = View.VISIBLE
        speciesError.errorImage.visibility = View.GONE
        speciesError.errorText.text = errorMessage
        speciesError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun SpeciesFragmentBinding.setNotEmpty() {
        speciesError.root.visibility = View.GONE
        speciesLoading.root.visibility = View.GONE
        speciesContent.visibility = View.VISIBLE
    }

}
