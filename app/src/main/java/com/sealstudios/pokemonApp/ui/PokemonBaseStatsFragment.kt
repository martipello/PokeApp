package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.akexorcist.roundcornerprogressbar.common.AnimatedRoundCornerProgressBar
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonBaseStats.Companion.baseStatsTotal
import com.sealstudios.pokemonApp.database.`object`.PokemonWithBaseStats
import com.sealstudios.pokemonApp.databinding.PokemonBaseStatsFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.viewModel.ColorViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonBaseStatsViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PokemonBaseStatsFragment : Fragment() {

    private val pokemonBaseStatsViewModel: PokemonBaseStatsViewModel by viewModels({ requireParentFragment() })
    private val colorViewModel: ColorViewModel by viewModels({ requireParentFragment() })

    private var _binding: PokemonBaseStatsFragmentBinding? = null
    private val binding get() = _binding!!


    private var dominantAndLightVibrantColors: Pair<Int, Int> = 0 to 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PokemonBaseStatsFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeColor()
        observePokemonBaseStats()
    }

    private fun observePokemonBaseStats() {
        pokemonBaseStatsViewModel.pokemonWithStats.observe(
                viewLifecycleOwner,
                Observer { pokemonWithStats ->
                    when (pokemonWithStats.status) {
                        Status.SUCCESS -> {
                            if (pokemonWithStats.data != null) {
                                populateBaseStatsGraph(pokemonWithStats.data)
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

    private fun observeColor() {
        colorViewModel.dominantAndLightVibrantColors.observe(viewLifecycleOwner, Observer {
            dominantAndLightVibrantColors = it
        })
    }

    private fun populateBaseStatsGraph(data: PokemonWithBaseStats) {
        lifecycleScope.launch {
            setStatBar(binding.hpValue)
            setStatBar(binding.attackValue)
            setStatBar(binding.defenceValue)
            setStatBar(binding.speedValue)
            setStatBar(binding.specialAttackValue)
            setStatBar(binding.specialDefenceValue)
            animateBaseStatBars(data)
        }
    }

    private fun animateBaseStatBars(data: PokemonWithBaseStats) {
        lifecycleScope.launch {
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.hpValue, data.pokemonBaseStats?.hp?.toFloat() ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.attackValue, data.pokemonBaseStats?.attack?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.defenceValue, data.pokemonBaseStats?.defense?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.speedValue, data.pokemonBaseStats?.speed?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.specialAttackValue, data.pokemonBaseStats?.specialAttack?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.specialDefenceValue, data.pokemonBaseStats?.specialDefense?.toFloat()
                        ?: 0f)
            }
        }
    }

    private fun setStatBar(bar: AnimatedRoundCornerProgressBar) {
        bar.enableAnimation()
        bar.progressColor = dominantAndLightVibrantColors.lightVibrantColor
        bar.progressBackgroundColor = dominantAndLightVibrantColors.dominantColor
        bar.animationSpeedScale = 5f
    }

    private fun setStatBarProgress(bar: AnimatedRoundCornerProgressBar, value: Float) {
        bar.enableAnimation()
        bar.progressBackgroundColor =
                if (dominantAndLightVibrantColors.dominantColor != dominantAndLightVibrantColors.lightVibrantColor)
                    dominantAndLightVibrantColors.dominantColor
                else ContextCompat.getColor(binding.root.context, R.color.light_grey)
        bar.progressColor = dominantAndLightVibrantColors.lightVibrantColor
        bar.animationSpeedScale = 5f
        lifecycleScope.launch {
            delay(500)
            withContext(Dispatchers.Main) {
                bar.progress = value
            }
        }
    }

    private fun populateBaseStats(data: PokemonWithBaseStats) {
        binding.totalLabel.text = resources.getString(R.string.base_stats_total, data.pokemonBaseStats?.baseStatsTotal())
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
        baseStatsError.errorImage.visibility = View.GONE
        baseStatsError.root.visibility = View.VISIBLE
        baseStatsError.errorText.text = errorMessage
        baseStatsError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonBaseStatsFragmentBinding.setNotEmpty() {
        baseStatsError.root.visibility = View.GONE
        baseStatsLoading.root.visibility = View.GONE
        baseStatsRoot.visibility = View.VISIBLE
        baseStatsContent.visibility = View.VISIBLE
    }

    private fun PokemonBaseStatsFragmentBinding.setEmpty() {
        baseStatsRoot.visibility = View.GONE
    }

}
