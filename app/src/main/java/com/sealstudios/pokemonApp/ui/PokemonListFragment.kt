package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPokemonDetailFragment
import com.sealstudios.pokemonApp.ui.adapter.AdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapter
import com.sealstudios.pokemonApp.ui.util.*
import com.sealstudios.pokemonApp.ui.util.customViews.fabFilter.animation.ScrollAwareFilerFab
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PokemonListFragment : Fragment(), AdapterClickListener, FilterChipClickListener {

    @Inject
    lateinit var glide: RequestManager
    private val binding get() = _binding!!
    private lateinit var pokemonAdapter: PokemonAdapter
    private var _binding: PokemonListFragmentBinding? = null
    private val pokemonListViewModel: PokemonListViewModel by viewModels()
    private val pokemonDetailViewModel: PokemonDetailViewModel
            by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonListFragmentBinding.inflate(inflater, container, false)
        setInsets()
        return binding.root
    }

    private fun setInsets() {
        binding.pokemonListFragmentCollapsingAppBar.appBarLayout.alignBelowStatusBar()
        binding.pokemonListFragmentCollapsingAppBar.toolbar.addSystemWindowInsetToPadding(top = false)
        binding.pokemonListFragmentCollapsingAppBar.toolbarLayout.addSystemWindowInsetToPadding(top = false)
        binding.pokemonListFragmentContent.pokemonListRecyclerView.addSystemWindowInsetToPadding(bottom = true)
        binding.pokemonListFilter.filterFab.addSystemWindowInsetToMargin(
            bottom = true,
            right = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        setToolbarTitleExpandedColor(view.context)
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpPokemonAdapter()
        setUpPokemonRecyclerView(view.context)
        observeFilters()
        observePokemonList()
        setUpViews()
        addScrollAwarenessForFilterButton()
    }

    private fun setUpViews() {
        binding.pokemonListFilter.filterFab.setOnClickListener {
            animateFilterFab()
        }
    }

    private fun setUpFilterView(selections: MutableMap<String, Boolean>) {
        val chipGroup = binding.pokemonListFilter.filterGroup.root
        chipGroup.chipSpacingHorizontal = 96.dp
        chipGroup.chipSpacingVertical = 8.dp
        FilterGroupHelper(
            chipGroup = chipGroup,
            clickListener = this@PokemonListFragment,
            selections = selections
        ).bindChips()
    }

    private fun animateFilterFab() {

    }

    private fun addScrollAwarenessForFilterButton() {
        ScrollAwareFilerFab(
            circleCardView = binding.pokemonListFilter.filterFab,
            recyclerView = binding.pokemonListFragmentContent.pokemonListRecyclerView,
            circleCardViewParent = binding.listFragmentContainer
        ).start()
    }

    private fun setUpPokemonAdapter() {
        pokemonAdapter = PokemonAdapter(clickListener = this, glide = glide)
    }

    private fun observePokemonList() {
        pokemonListViewModel.searchPokemon.observe(viewLifecycleOwner, Observer { pokemonList ->
            pokemonList?.let { pokemonListWithTypesAndSpecies ->
                pokemonAdapter.submitList(pokemonListWithTypesAndSpecies)
                checkForEmptyLayout(pokemonListWithTypesAndSpecies)
            }
        })
    }

    private fun observeFilters() {
        pokemonListViewModel.filters.observe(viewLifecycleOwner, Observer { selectionsLiveData ->
            selectionsLiveData?.let { selections ->
                setUpFilterView(selections)
            }
        })
    }

    private fun checkForEmptyLayout(it: List<PokemonWithTypesAndSpecies>) {
        val content = binding.pokemonListFragmentContent
        binding.pokemonListFragmentContent.emptyPokemonList.pokemonListLoading.visibility =
            View.GONE
        if (it.isNotEmpty()) {
            content.emptyPokemonList.emptyResultsImage.visibility = View.GONE
            content.emptyPokemonList.emptyResultsText.visibility = View.GONE
        } else {
            content.emptyPokemonList.emptyResultsImage.visibility = View.VISIBLE
            content.emptyPokemonList.emptyResultsText.visibility = View.VISIBLE
        }
    }

    private fun setUpPokemonRecyclerView(context: Context) {
        val recyclerView = binding.pokemonListFragmentContent.pokemonListRecyclerView
        recyclerView.addItemDecoration(
            PokemonListDecoration(
                context.resources.getDimensionPixelSize(
                    R.dimen.small_margin_8dp
                )
            )
        )
        recyclerView.adapter = pokemonAdapter
    }

    private fun setActionBar() {
        val toolbar = binding.pokemonListFragmentCollapsingAppBar.toolbar
        val mainActivity = (activity as AppCompatActivity)
        mainActivity.setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(
            mainActivity,
            findNavController(this@PokemonListFragment)
        )
    }

    private fun setToolbarTitleExpandedColor(context: Context) {
        binding.pokemonListFragmentCollapsingAppBar.toolbarLayout.setExpandedTitleColor(
            ContextCompat.getColor(
                context,
                android.R.color.transparent
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as? SearchManager
        (menu.findItem(R.id.search).actionView as androidx.appcompat.widget.SearchView).apply {
            this.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
            setQueryListener(searchView = this)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setQueryListener(searchView: androidx.appcompat.widget.SearchView) {
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    pokemonListViewModel.setSearch("%$it%")
                }
                return false
            }
        })
    }

    private fun navigateToDetailFragment(name: String) {
        val action = actionPokemonListFragmentToPokemonDetailFragment(pokemonName = name)
        findNavController(this@PokemonListFragment).navigate(action)
    }

    override fun onItemSelected(position: Int, item: Pokemon) {
        pokemonDetailViewModel.setId(item.id)
        navigateToDetailFragment(item.name)
    }

    override fun onFilterSelected(key: String, value: Boolean) {
        pokemonListViewModel.setFilter(key, value)
    }

}