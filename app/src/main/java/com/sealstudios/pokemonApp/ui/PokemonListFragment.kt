package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.card.MaterialCardView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.RemotePokemonToRoomPokemonRepository
import com.sealstudios.pokemonApp.database.`object`.PokemonForList
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPokemonDetailFragment
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.ScrollAwareFilerFab
import com.sealstudios.pokemonApp.ui.insets.PokemonListFragmentInsets
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitEnd
import com.sealstudios.pokemonApp.ui.util.FilterChipClickListener
import com.sealstudios.pokemonApp.ui.util.FilterGroupHelper
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.util.dp
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import com.sealstudios.pokemonApp.util.SharedPreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PokemonListFragment : Fragment(),
    PokemonAdapterClickListener, FilterChipClickListener {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    @Inject
    lateinit var remotePokemonToRoomPokemonRepository: RemotePokemonToRoomPokemonRepository

    private val binding get() = _binding!!
    private var _binding: PokemonListFragmentBinding? = null

    private var search: String = ""
    private var filterIsExpanded = false
    private val pokemonListViewModel: PokemonListViewModel by viewModels()
    private lateinit var pokemonAdapter: PokemonAdapter

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFilterIsExpandedFromSavedInstanceState(savedInstanceState)
        _binding = PokemonListFragmentBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        PokemonListFragmentInsets().setInsets(binding)
        observeAnimationState()
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpPokemonAdapter()
        setUpPokemonRecyclerView(view.context)
        checkIsFirstTime()
        observeFilters()
        observePokemonList()
        observeSearch()
        setUpViews()
        addScrollAwarenessForFilterFab()
    }

    private fun checkIsFirstTime() {
        if (sharedPreferenceHelper.getBool(SharedPreferenceHelper.isFirstTime)) {
            remotePokemonToRoomPokemonRepository.getAllPokemon()
            sharedPreferenceHelper.setBool(SharedPreferenceHelper.isFirstTime, false)
        }
    }

    private fun setFilterIsExpandedFromSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            filterIsExpanded = savedInstanceState.getBoolean(isFiltersLayoutExpandedKey)
        }
    }

    private fun setUpViews() {
        binding.pokemonListFilter.filterFab.setOnClickListener {
            if (!filterIsExpanded) {
                showFiltersAnimation()
            }
        }
    }

    private fun hideFiltersAnimation() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.pokemonListFilter.filterHolder.circleHide().run {
                start()
                awaitEnd()
                binding.pokemonListFilter.filterHolder.visibility = View.INVISIBLE
                binding.pokemonListFilter.filterFab.arcAnimateFilterFabOut(null).run {
                    start()
                    awaitEnd()
                    if (filterIsExpanded) {
                        pokemonListViewModel.setFiltersLayoutExpanded(false)
                    }
                }
            }
        }
    }

    private fun showFiltersAnimation() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.pokemonListFilter.filterFab.arcAnimateFilterFabIn(
                binding.pokemonListFilter.filterHolder,
                null
            ).run {
                start()
                awaitEnd()
                binding.pokemonListFilter.filterHolder.circleReveal().run {
                    start()
                    awaitEnd()
                    if (!filterIsExpanded) {
                        pokemonListViewModel.setFiltersLayoutExpanded(true)
                    }
                }
            }
        }
    }

    private fun setUpFilterView(selections: MutableSet<String>) {
        binding.pokemonListFilter.closeFiltersButton.setOnClickListener {
            if (filterIsExpanded) {
                hideFiltersAnimation()
            }
        }
        val chipGroup = binding.pokemonListFilter.filterGroupLayout.root
        chipGroup.chipSpacingHorizontal = 96.dp
        chipGroup.chipSpacingVertical = 8.dp
        FilterGroupHelper(
            chipGroup = chipGroup,
            clickListener = this@PokemonListFragment,
            selections = selections
        ).bindChips()
    }

    private fun addScrollAwarenessForFilterFab() {
        ScrollAwareFilerFab(
            circleCardView = binding.pokemonListFilter.filterFab,
            recyclerView = binding.pokemonListFragmentContent.pokemonListRecyclerView,
            circleCardViewParent = binding.listFragmentContainer,
            scrollAwareFilterFabAnimationListener = null
        ).start()
    }

    private fun setUpPokemonAdapter() {
        pokemonAdapter = PokemonAdapter(clickListener = this, glide = glide)
    }

    private fun observePokemonList() {
        pokemonListViewModel.searchPokemon.observe(
            viewLifecycleOwner, Observer { pokemonData ->
                Log.d("PLF", "Observer Observer Observer")
                pokemonData?.let {
                    pokemonAdapter.submitList(it)
                }
            })
    }

    private fun observeSearch() {
        pokemonListViewModel.search.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                search = it.replace("%", "")
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

    private fun observeAnimationState() {
        pokemonListViewModel.isFiltersLayoutExpanded.observe(
            viewLifecycleOwner, Observer { filterIsExpanded ->
                if (filterIsExpanded && this.filterIsExpanded) {
                    binding.root.post {
                        showFiltersAnimation()
                    }
                }
                this.filterIsExpanded = filterIsExpanded
            })
    }

    private fun setViewLoadingState() {
        binding.emptyPokemonList.pokemonListLoading.visibility = View.VISIBLE
        binding.emptyPokemonList.emptyResultsImage.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsText.visibility = View.GONE
        binding.errorPokemonList.errorText.visibility = View.GONE
        binding.errorPokemonList.retryButton.visibility = View.GONE
    }

    private fun setViewErrorState() {
        binding.emptyPokemonList.pokemonListLoading.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsImage.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsText.visibility = View.GONE
        binding.errorPokemonList.errorText.visibility = View.VISIBLE
        binding.errorPokemonList.retryButton.visibility = View.VISIBLE
    }

    private fun setViewEmptyState() {
        binding.emptyPokemonList.pokemonListLoading.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsImage.visibility = View.VISIBLE
        binding.emptyPokemonList.emptyResultsText.visibility = View.VISIBLE
        binding.errorPokemonList.errorText.visibility = View.GONE
        binding.errorPokemonList.retryButton.visibility = View.GONE
    }

    private fun setViewNotEmptyState() {
        binding.emptyPokemonList.pokemonListLoading.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsImage.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsText.visibility = View.GONE
        binding.errorPokemonList.errorText.visibility = View.GONE
        binding.errorPokemonList.retryButton.visibility = View.GONE
    }

    private fun setUpPokemonRecyclerView(context: Context) {
        binding.pokemonListFragmentContent.pokemonListRecyclerView.run {
            addRecyclerViewDecoration(this, context)
            adapter = pokemonAdapter
            doOnPreDraw {
                startPostponedEnterTransition()
            }
        }
    }

    private fun addRecyclerViewDecoration(
        recyclerView: RecyclerView,
        context: Context
    ) {
        recyclerView.addItemDecoration(
            PokemonListDecoration(
                context.resources.getDimensionPixelSize(
                    R.dimen.qualified_small_margin_8dp
                )
            )
        )
    }

    private fun setActionBar() {
        val topLevelDestinations: MutableSet<Int> = HashSet()
        topLevelDestinations.add(R.id.PokemonListFragment)
        val appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .build()
        val toolbar = binding.pokemonListFragmentCollapsingAppBar.toolbar
        val mainActivity = (activity as AppCompatActivity)
        mainActivity.setSupportActionBar(toolbar)
        setupActionBarWithNavController(mainActivity, appBarConfiguration)
        setToolbarTitleExpandedColor(toolbar.context)
    }

    private fun setupActionBarWithNavController(
        mainActivity: AppCompatActivity,
        appBarConfiguration: AppBarConfiguration
    ) {
        NavigationUI.setupActionBarWithNavController(
            mainActivity,
            findNavController(this@PokemonListFragment),
            appBarConfiguration
        )
    }

    private fun setToolbarTitleExpandedColor(context: Context) {
        binding.pokemonListFragmentCollapsingAppBar.toolbarLayout
            .setExpandedTitleColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.transparent
                )
            )
    }

    private fun SearchView.restoreSearchUIState(menu: Menu) {
        if (search.isNotEmpty()) {
            this.isIconified = true
            this.onActionViewExpanded()
            this.setQuery(search, false)
            this.isFocusable = true
            val searchMenuItem = menu.findItem(R.id.search)
            searchMenuItem.expandActionView()
        }
    }

    private fun setQueryListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
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

    private fun navigateToDetailFragment(name: String, view: View) {
        view as MaterialCardView
        val action = actionPokemonListFragmentToPokemonDetailFragment(
            pokemonName = name,
            transitionName = view.transitionName,
            dominantSwatchRgb = view.cardBackgroundColor.defaultColor,
            lightVibrantSwatchRgb = view.strokeColorStateList?.defaultColor
                ?: ContextCompat.getColor(view.context, R.color.white)
        )
        val extras = FragmentNavigatorExtras(
            view to view.transitionName
        )
        if (filterIsExpanded) {
            hideFiltersAnimation()
        }
        navigate(action, extras)
    }

    private fun navigate(destination: NavDirections, extraInfo: FragmentNavigator.Extras) =
        with(findNavController()) {
            currentDestination?.getAction(destination.actionId)
                ?.let { navigate(destination, extraInfo) }
        }

    override fun onItemSelected(pokemon: PokemonForList, view: View) {
        navigateToDetailFragment(pokemon.name, view)
    }

    override fun onFilterSelected(key: String, value: Boolean) {
        if (value) {
            pokemonListViewModel.addFilter(key)
        } else {
            pokemonListViewModel.removeFilter(key)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as? SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            this.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
            restoreSearchUIState(menu)
            setQueryListener(searchView = this)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFiltersLayoutExpandedKey, filterIsExpanded)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val isFiltersLayoutExpandedKey: String = "isFiltersLayoutExpanded"
    }
}


