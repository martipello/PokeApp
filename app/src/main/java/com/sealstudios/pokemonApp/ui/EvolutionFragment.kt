package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.databinding.EvolutionFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.viewModel.EvolutionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EvolutionFragment : Fragment() {
    private val evolutionViewModel: EvolutionViewModel by viewModels({ requireParentFragment() })
    private var _binding: EvolutionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EvolutionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEvolutionChain()
    }

    private fun observeEvolutionChain() {
        evolutionViewModel.evolution.observe(viewLifecycleOwner, { evolution ->
            when (evolution.status) {
                Status.SUCCESS -> {
                    Log.d("EVOLUTION", "SUCCESS ${evolution.data}")
                }
                Status.ERROR -> {
                    binding.setError(evolution.message ?: "Oops, something went wrong...")
                    { evolutionViewModel.retry() }
                }
                Status.LOADING -> binding.setLoading()
            }
        })
    }

    private fun EvolutionFragmentBinding.setLoading() {
        evolutionContent.visibility = View.GONE
        evolutionError.root.visibility = View.GONE
        evolutionLoading.root.visibility = View.VISIBLE
        evolutionLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun EvolutionFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        evolutionLoading.root.visibility = View.GONE
        evolutionContent.visibility = View.GONE
        evolutionError.root.visibility = View.VISIBLE
        evolutionError.errorImage.visibility = View.GONE
        evolutionError.errorText.text = errorMessage
        evolutionError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun EvolutionFragmentBinding.setNotEmpty() {
        evolutionError.root.visibility = View.GONE
        evolutionLoading.root.visibility = View.GONE
        evolutionContent.visibility = View.VISIBLE
    }

}
