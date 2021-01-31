package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.material.card.MaterialCardView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.ads.AdsManager
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.api.states.ErrorCodes
import com.sealstudios.pokemonApp.database.`object`.MyNativeAd
import com.sealstudios.pokemonApp.database.`object`.PokemonForList
import com.sealstudios.pokemonApp.database.`object`.objectInterface.PokemonAdapterListItem
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPokemonDetailFragment
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.ScrollAwareFilerFab
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.insets.PokemonListFragmentInsets
import com.sealstudios.pokemonApp.ui.listenerExtensions.awaitEnd
import com.sealstudios.pokemonApp.ui.util.FilterChipClickListener
import com.sealstudios.pokemonApp.ui.util.FilterGroupHelper
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.util.dp
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import com.sealstudios.pokemonApp.ui.viewModel.RemotePokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PokemonListFragment : Fragment(),
    PokemonAdapterClickListener, FilterChipClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var ads: MutableList<PokemonAdapterListItem> = mutableListOf()

    @Inject
    lateinit var glide: RequestManager

    private val binding get() = _binding!!
    private var _binding: PokemonListFragmentBinding? = null

    private var search: String = ""
    private var filterIsExpanded = false
    private val pokemonListViewModel: PokemonListViewModel by viewModels()
    private val remotePokemonViewModel: RemotePokemonViewModel by viewModels()
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
        observeFetchAllPokemonResponse()
        observeFilters()
        observeSearch()
        setUpViews()
        addScrollAwarenessForFilterFab()
        createAds(view)
    }

    private fun createAds(view: View) {
        val adsManager = AdsManager(view.context)
        adsManager.createAds(listener = UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {
            ads.add(MyNativeAd(it))
            if (!adsManager.getAdLoader().isLoading) {
                pokemonAdapter.submitList(ads)
            }
            if (activity?.isDestroyed == true || activity?.isFinishing == true || activity?.isChangingConfigurations == true) {
                it.destroy()
                return@OnUnifiedNativeAdLoadedListener
            }
        })
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

    private fun observeFetchAllPokemonResponse() {
        remotePokemonViewModel.allPokemonResponse.observe(viewLifecycleOwner, Observer { allPokemon ->
            when (allPokemon.status) {
                Status.SUCCESS -> {
                    binding.setNotEmpty()
                    observePokemonList()
                }
                Status.ERROR -> {
                    if (allPokemon.code == ErrorCodes.NO_CONNECTION.code) {
                        if (remotePokemonViewModel.hasFetchedPartialPokemonData) {
                            binding.setNotEmpty()
                            observePokemonList()
                        } else {
                            binding.setError(
                                ErrorCodes.NO_CONNECTION.message
                            ) { remotePokemonViewModel.fetchAllPokemon() }
                        }
                    } else {
                        binding.setError(
                            allPokemon.message ?: getString(R.string.error_text)
                        ) { remotePokemonViewModel.fetchAllPokemon() }
                    }
                }
                Status.LOADING -> binding.setLoading()
            }
        })
    }

    private fun observePokemonList() {
        pokemonListViewModel.searchPokemon.observe(
            viewLifecycleOwner, Observer { pokemonData ->
                if (pokemonData != null) {
                    if (pokemonData.isEmpty()) {
                        binding.setEmpty()
                    } else {
                        binding.setNotEmpty()
                    }
                    pokemonAdapter.submitList(pokemonData)
                } else {
                    binding.setLoading()
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

    private fun setUpPokemonRecyclerView(context: Context) {
        binding.pokemonListFragmentContent.pokemonListRecyclerView.run {
            addRecyclerViewDecoration(this, context)
            this.setHasFixedSize(true)
            binding.pokemonListFragmentContent.swipeRefreshPokemonList.setOnRefreshListener(this@PokemonListFragment)
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
        ads.clear()
    }

    override fun onRefresh() {
        remotePokemonViewModel.setFetchedPartialPokemonData(false)
        remotePokemonViewModel.fetchAllPokemon()
    }

    private fun hideEmptyLayout() {
        binding.emptyPokemonList.emptyResultsImage.visibility = View.GONE
        binding.emptyPokemonList.emptyResultsText.visibility = View.GONE
    }

    private fun hideErrorLayout() {
        binding.errorPokemonList.root.visibility = View.GONE
    }

    private fun hideLoadingLayout() {
        binding.emptyPokemonList.pokemonListLoading.root.visibility = View.GONE
    }

    private fun PokemonListFragmentBinding.setLoading() {
        emptyPokemonList.pokemonListLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
        emptyPokemonList.pokemonListLoading.root.visibility = View.VISIBLE
        hideEmptyLayout()
        hideErrorLayout()
    }

    private fun PokemonListFragmentBinding.setError(errorMessage: String, fetchAllPokemon: () -> Unit) {
        pokemonListFragmentContent.swipeRefreshPokemonList.isRefreshing = false
        hideEmptyLayout()
        hideLoadingLayout()
        errorPokemonList.errorImage.visibility = View.VISIBLE
        errorPokemonList.errorText.text = errorMessage
        errorPokemonList.retryButton.setOnClickListener {
            fetchAllPokemon()
        }

    }

    private fun PokemonListFragmentBinding.setEmpty() {
        pokemonListFragmentContent.swipeRefreshPokemonList.isRefreshing = false
        hideErrorLayout()
        hideLoadingLayout()
        emptyPokemonList.emptyResultsImage.visibility = View.VISIBLE
        emptyPokemonList.emptyResultsText.visibility = View.VISIBLE
    }

    private fun PokemonListFragmentBinding.setNotEmpty() {
        pokemonListFragmentContent.swipeRefreshPokemonList.isRefreshing = false
        hideEmptyLayout()
        hideErrorLayout()
        hideLoadingLayout()
    }

    companion object {
        private const val isFiltersLayoutExpandedKey: String = "isFiltersLayoutExpanded"
    }

}


