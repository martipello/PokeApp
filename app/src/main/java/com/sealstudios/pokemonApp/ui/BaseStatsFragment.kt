package com.sealstudios.pokemonApp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.BaseStats.Companion.baseStatsTotal
import com.sealstudios.pokemonApp.database.`object`.relations.PokemonWithBaseStats
import com.sealstudios.pokemonApp.databinding.BaseStatsFragmentBinding
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.viewModel.ColorViewModel
import com.sealstudios.pokemonApp.ui.viewModel.BaseStatsViewModel
import com.sealstudios.pokemonApp.ui.viewModel.dominantColor
import com.sealstudios.pokemonApp.ui.viewModel.lightVibrantColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class BaseStatsFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val baseStatsViewModel: BaseStatsViewModel by viewModels({ requireParentFragment() })
    private val colorViewModel: ColorViewModel by viewModels({ requireParentFragment().requireParentFragment() })

    private var _binding: BaseStatsFragmentBinding? = null
    private val binding get() = _binding!!


    private var dominantAndLightVibrantColors: Pair<Int, Int> = 0 to 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BaseStatsFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeColor()
        observePokemonBaseStats()
    }

    private fun observePokemonBaseStats() {
        baseStatsViewModel.pokemonWithStats.observe(
                viewLifecycleOwner,
                { pokemonWithStats ->
                    when (pokemonWithStats.status) {
                        Status.SUCCESS -> {
                            if (pokemonWithStats.data?.baseStats != null) {
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
                            { baseStatsViewModel.retry() }
                        }
                        Status.LOADING -> {
                            binding.setLoading()
                        }
                    }
                })
    }

    private fun observeColor() {
        colorViewModel.dominantAndLightVibrantColors.observe(viewLifecycleOwner, {
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
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.hpValue, data.baseStats?.hp?.toFloat() ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.attackValue, data.baseStats?.attack?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.defenceValue, data.baseStats?.defense?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.speedValue, data.baseStats?.speed?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.specialAttackValue, data.baseStats?.specialAttack?.toFloat()
                        ?: 0f)
            }
            delay(200)
            withContext(Dispatchers.Main) {
                setStatBarProgress(binding.specialDefenceValue, data.baseStats?.specialDefense?.toFloat()
                        ?: 0f)
            }
        }
    }

    private fun setStatBar(bar: TextRoundCornerProgressBar) {
        bar.progressBackgroundColor =
                if (dominantAndLightVibrantColors.dominantColor != dominantAndLightVibrantColors.lightVibrantColor)
                    dominantAndLightVibrantColors.dominantColor
                else ContextCompat.getColor(binding.root.context, R.color.light_grey)
        bar.progressColors = intArrayOf(
                ColorUtils.blendARGB(
                        dominantAndLightVibrantColors.lightVibrantColor,
                        Color.WHITE, 0.5f),
                dominantAndLightVibrantColors.lightVibrantColor
        )
    }

    private fun setStatBarProgress(bar: TextRoundCornerProgressBar, value: Float) {
        lifecycleScope.launch {
            withContext(Dispatchers.Default){
                val format = DecimalFormat()
                format.isDecimalSeparatorAlwaysShown = false
                delay(200)
                withContext(Dispatchers.Main) {
                    bar.progress = value
                    bar.progressText = format.format(value)
                    bar.textProgressSize = 32
                }
            }
        }
    }

    private fun populateBaseStats(data: PokemonWithBaseStats) {
        binding.totalLabel.text = resources.getString(R.string.base_stats_total, data.baseStats?.baseStatsTotal())
    }

    private fun BaseStatsFragmentBinding.setLoading() {
        baseStatsContent.visibility = View.GONE
        baseStatsError.root.visibility = View.GONE
        baseStatsEmpty.root.visibility = View.GONE
        baseStatsLoading.root.visibility = View.VISIBLE
        baseStatsLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun BaseStatsFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        baseStatsContent.visibility = View.GONE
        baseStatsError.errorImage.visibility = View.GONE
        baseStatsEmpty.root.visibility = View.GONE
        baseStatsError.root.visibility = View.VISIBLE
        baseStatsError.errorText.text = errorMessage
        baseStatsError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun BaseStatsFragmentBinding.setNotEmpty() {
        baseStatsError.root.visibility = View.GONE
        baseStatsLoading.root.visibility = View.GONE
        baseStatsEmpty.root.visibility = View.GONE
        baseStatsContent.visibility = View.VISIBLE
    }

    private fun BaseStatsFragmentBinding.setEmpty() {
        baseStatsError.root.visibility = View.GONE
        baseStatsLoading.root.visibility = View.GONE
        baseStatsContent.visibility = View.GONE
        baseStatsEmpty.root.visibility = View.VISIBLE
        baseStatsEmpty.emptyResultsText.text = context?.getString(R.string.no_base_stats)
        glide.load(R.drawable.no_results_snorlax).into(baseStatsEmpty.emptyResultsImage)
    }

}
