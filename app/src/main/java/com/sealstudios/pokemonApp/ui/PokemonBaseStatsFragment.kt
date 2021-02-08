package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats.Companion.baseStatsTotal
import com.sealstudios.pokemonApp.database.`object`.PokemonWithBaseStats
import com.sealstudios.pokemonApp.databinding.PokemonBaseStatsFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.viewModel.PokemonBaseStatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonBaseStatsFragment : Fragment() {

    private val pokemonBaseStatsViewModel: PokemonBaseStatsViewModel by viewModels({ requireParentFragment() })

    private var _binding: PokemonBaseStatsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PokemonBaseStatsFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemonBaseStats()
    }

    private fun observePokemonBaseStats() {
        pokemonBaseStatsViewModel.pokemonWithStats.observe(
                viewLifecycleOwner,
                Observer { pokemonWithStats ->
                    when (pokemonWithStats.status) {
                        Status.SUCCESS -> {
                            if (pokemonWithStats.data != null) {
                                populateBaseStats(pokemonWithStats.data)
                                binding.setNotEmpty()
                            } else {
                                binding.setEmpty()
                            }
                        }
                        Status.ERROR -> {
                            binding.setError(
                                    pokemonWithStats.message
                                            ?: "Oops, something went wrong..."
                            )
                            { pokemonBaseStatsViewModel.retry() }
                        }
                        Status.LOADING -> {
                            binding.setLoading()
                        }
                    }
                })
    }

    private fun populateBaseStats(data: PokemonWithBaseStats) {
        binding.hpText.text = "${data.pokemonBaseStats?.hp}"
        binding.attackText.text = "${data.pokemonBaseStats?.attack}"
        binding.defenceText.text = "${data.pokemonBaseStats?.defense}"
        binding.specialAttackText.text = "${data.pokemonBaseStats?.specialAttack}"
        binding.specialDefenceText.text = "${data.pokemonBaseStats?.specialDefense}"
        binding.speedText.text = "${data.pokemonBaseStats?.speed}"
        binding.totalLabel.text = "${data.pokemonBaseStats?.baseStatsTotal()}"
    }

    private fun PokemonBaseStatsFragmentBinding.setLoading() {
        baseStatsContent.visibility = View.GONE
        baseStatsError.root.visibility = View.GONE
        baseStatsLoading.root.visibility = View.VISIBLE
        baseStatsLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonBaseStatsFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        baseStatsLoading.root.visibility = View.GONE
        baseStatsContent.visibility = View.GONE
        baseStatsError.root.visibility = View.VISIBLE
        baseStatsError.errorImage.visibility = View.GONE
        baseStatsError.errorText.text = errorMessage
        baseStatsError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonBaseStatsFragmentBinding.setNotEmpty() {
        baseStatsError.root.visibility = View.GONE
        baseStatsLoading.root.visibility = View.GONE
        baseStatsContent.visibility = View.VISIBLE
    }

    private fun PokemonBaseStatsFragmentBinding.setEmpty() {
        root.visibility = View.GONE
    }

}
