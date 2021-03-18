package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentFilterHolderBinding
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.ScrollAwareFilerFab
import com.sealstudios.pokemonApp.ui.insets.PokemonFilterFragmentInsets
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitEnd
import com.sealstudios.pokemonApp.ui.util.FilterChipClickListener
import com.sealstudios.pokemonApp.ui.util.FilterGroupHelper
import com.sealstudios.pokemonApp.ui.util.dp
import com.sealstudios.pokemonApp.ui.viewModel.PokemonFiltersViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonFiltersFragment : Fragment(), FilterChipClickListener {

    private val binding get() = _binding!!
    private var _binding: PokemonListFragmentFilterHolderBinding? = null

    private var filterIsExpanded = false

    private val pokemonFiltersViewModel: PokemonFiltersViewModel by viewModels({ requireActivity() })
    @FlowPreview
    private val pokemonListViewModel: PokemonListViewModel by viewModels({ requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (filterIsExpanded) {
                hideFiltersAnimation()
            } else {
                this.remove()
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setFilterIsExpandedFromSavedInstanceState(savedInstanceState)
        _binding = PokemonListFragmentFilterHolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeAnimationState()
        PokemonFilterFragmentInsets().setInsets(binding)
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        observeFilters()
        onAddScrollAwareFilerFab()
        onCloseFiltersLayout()
    }

    private fun observeFilters() {
        pokemonListViewModel.filters.observe(viewLifecycleOwner, { selectionsLiveData ->
            selectionsLiveData?.let { selections ->
                setUpFilterView(selections)
            }
        })
    }

    private fun onAddScrollAwareFilerFab() {
        pokemonFiltersViewModel.onAddScrollAwareFilerFab.observe(viewLifecycleOwner, {
            it?.let {
                addScrollAwarenessForFilterFab(it)
            }
        })
    }

    private fun onCloseFiltersLayout() {
        pokemonFiltersViewModel.onCloseFiltersLayout.observe(viewLifecycleOwner, {
            if (filterIsExpanded) {
                hideFiltersAnimation()
            }
        })
    }

    private fun addScrollAwarenessForFilterFab(recyclerView: RecyclerView) {
        ScrollAwareFilerFab(
                circleCardView = binding.filterFab,
                recyclerView = recyclerView,
                circleCardViewParent = binding.root,
                scrollAwareFilterFabAnimationListener = null
        ).start()
    }

    private fun observeAnimationState() {
        pokemonFiltersViewModel.isFiltersLayoutExpanded.observe(
                viewLifecycleOwner, { filterIsExpanded ->
            if (filterIsExpanded && this.filterIsExpanded) {
                binding.root.post {
                    showFiltersAnimation()
                }
            }
            this.filterIsExpanded = filterIsExpanded
        })
    }

    private fun setFilterIsExpandedFromSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            filterIsExpanded = savedInstanceState.getBoolean(isFiltersLayoutExpandedKey)
        }
    }

    private fun setUpViews() {
        binding.filterFab.setOnClickListener {
            if (!filterIsExpanded) {
                showFiltersAnimation()
            }
        }
        binding.closeFiltersButton.setOnClickListener {
            if (filterIsExpanded) {
                hideFiltersAnimation()
            }
        }
        binding.clearFilters.pokemonTypeChip.setOnClickListener {
            pokemonListViewModel.clearFilters()
        }
        binding.filterGroupLayout.root.chipSpacingHorizontal = 96.dp
        binding.filterGroupLayout.root.chipSpacingVertical = 8.dp
    }

    private fun hideFiltersAnimation() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.filterHolder.circleHide().run {
                start()
                awaitEnd()
                binding.filterHolder.visibility = View.INVISIBLE
                binding.filterFab.arcAnimateFilterFabOut(null).run {
                    start()
                    awaitEnd()
                    if (filterIsExpanded) {
                        pokemonFiltersViewModel.setFiltersLayoutExpanded(false)
                    }
                }
            }
        }
    }

    private fun showFiltersAnimation() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.filterFab.arcAnimateFilterFabIn(
                    binding.filterHolder,
                    null
            ).run {
                start()
                awaitEnd()
                binding.filterHolder.circleReveal().run {
                    start()
                    awaitEnd()
                    if (!filterIsExpanded) {
                        pokemonFiltersViewModel.setFiltersLayoutExpanded(true)
                    }
                }
            }
        }
    }

    private fun setUpFilterView(selections: MutableSet<String>) {
        lifecycleScope.launch {
            FilterGroupHelper(
                    chipGroup = binding.filterGroupLayout.root,
                    clickListener = this@PokemonFiltersFragment,
                    selections = selections
            ).bindChips()
        }
    }

    override fun onFilterSelected(key: String, value: Boolean) {
        if (value) {
            pokemonListViewModel.addFilter(key)
        } else {
            pokemonListViewModel.removeFilter(key)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFiltersLayoutExpandedKey, filterIsExpanded)
    }

    companion object {
        private const val isFiltersLayoutExpandedKey: String = "isFiltersLayoutExpanded"
    }

}