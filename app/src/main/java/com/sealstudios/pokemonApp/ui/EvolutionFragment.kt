package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.relations.EvolutionChainWithDetailList
import com.sealstudios.pokemonApp.databinding.EvolutionFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.EvolutionAdapter
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.extensions.removeItemDecorations
import com.sealstudios.pokemonApp.ui.util.decorators.ListDividerDecoration
import com.sealstudios.pokemonApp.ui.viewModel.EvolutionViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EvolutionFragment : Fragment() {
    private val evolutionViewModel: EvolutionViewModel by viewModels({ requireParentFragment() })
    private var _binding: EvolutionFragmentBinding? = null
    private var evolutionAdapter: EvolutionAdapter? = null

    @Inject
    lateinit var glide: RequestManager

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EvolutionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setLoading()
        setUpEvolutionAdapter()
        setUpEvolutionRecyclerView()
        observeEvolutionChain()
    }

    private fun setUpEvolutionAdapter() {
        evolutionAdapter = EvolutionAdapter(glide)
    }

    private fun setUpEvolutionRecyclerView() = with(binding.evolutionRecyclerView) {
        adapter = evolutionAdapter
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        removeItemDecorations()
        addItemDecoration(
                ListDividerDecoration(R.drawable.divider,
                        binding.root.context,
                        context.resources.getDimensionPixelSize(
                                R.dimen.qualified_small_margin_8dp
                        ))
        )
    }

    private fun observeEvolutionChain() {
        evolutionViewModel.evolution.observe(viewLifecycleOwner, { evolution ->
            Log.d("EVOLUTION_FRAG", "$evolution")
            when (evolution.status) {
                Status.SUCCESS -> {
                    if (evolution.data != null && evolution.data.evolutionChainLinkList.isNotEmpty()) {
                        showRelevantEvolutionDetails(evolution.data)
                        binding.setNotEmpty()
                    } else {
                        binding.setEmpty()
                    }
                }
                Status.ERROR -> {
                    binding.setError(evolution.message ?: "Oops, something went wrong...")
                    { evolutionViewModel.retry() }
                }
                Status.LOADING -> binding.setLoading()
            }
        })
    }

    private fun showRelevantEvolutionDetails(evolutionChainWithDetailList: EvolutionChainWithDetailList) {
        if (evolutionChainWithDetailList.evolutionChainLinkList.isNotEmpty()) {
            binding.noEvolutionLabel.visibility = View.GONE
            evolutionAdapter?.submitList(evolutionChainWithDetailList.evolutionChainLinkList)
        } else {
            binding.noEvolutionLabel.visibility = View.VISIBLE
        }
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

    private fun EvolutionFragmentBinding.setEmpty() {
        evolutionError.root.visibility = View.GONE
        evolutionLoading.root.visibility = View.GONE
        evolutionContent.visibility = View.VISIBLE
    }

}
