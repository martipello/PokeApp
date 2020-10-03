package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.database.`object`.PokemonWithTypesAndSpecies
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentContentBinding
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPokemonDetailFragment
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapter
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.FabFilterAnimationListener
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.ScrollAwareFilerFab
import com.sealstudios.pokemonApp.ui.util.*
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PokemonListFragment : Fragment(), PokemonAdapterClickListener, FilterChipClickListener,
    FabFilterAnimationListener {

    @Inject
    lateinit var glide: RequestManager
    private val binding get() = _binding!!
    private var _binding: PokemonListFragmentBinding? = null
    private lateinit var pokemonAdapter: PokemonAdapter
    private var search: String = ""
    private var filterIsExpanded = false
    private var filterIsExpanding = false
    private val pokemonListViewModel: PokemonListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (filterIsExpanded) {
                tryHideFiltersAnimation()
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
        _binding = PokemonListFragmentBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        setInsets()
        observeAnimationState()
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpPokemonAdapter()
        setUpPokemonRecyclerView(view.context)
        observeFilters()
        observePokemonList()
        observeSearch()
        setUpViews()
        addScrollAwarenessForFilterFab()
    }

    private fun setInsets() {
        val appBarLayout = binding.pokemonListFragmentCollapsingAppBar
        val filterLayout = binding.pokemonListFilter
        val listLayout = binding.pokemonListFragmentContent

        appBarLayout.appBarLayout.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            view.layoutParams = marginLayoutParams
        }

        appBarLayout.toolbar.doOnApplyWindowInsetPadding { view, windowInsets, initialPadding ->
            view.updatePadding(
                left = windowInsets.systemWindowInsetLeft + initialPadding.left,
                right = windowInsets.systemWindowInsetRight + initialPadding.right
            )
        }

        appBarLayout.toolbarLayout.doOnApplyWindowInsetPadding { _, _, _ ->
            //required or the views below do not get there padding updated
        }

        listLayout.pokemonListRecyclerView.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                right = windowInsets.systemWindowInsetRight,
                left = windowInsets.systemWindowInsetLeft,
                bottom = windowInsets.systemWindowInsetBottom
            )
        }

        filterLayout.filterGroupLayout.chipGroup.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                bottom = windowInsets.systemWindowInsetBottom,
                right = windowInsets.systemWindowInsetRight,
                left = windowInsets.systemWindowInsetLeft
            )
        }

        filterLayout.filterPokemonLabel.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                left = resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetLeft
            )
        }

        filterLayout.closeFiltersButton.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                right = windowInsets.systemWindowInsetRight
            )
        }

        filterLayout.filterFab.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.bottomMargin =
                resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetBottom
            marginLayoutParams.rightMargin =
                resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetRight
            view.layoutParams = marginLayoutParams
        }

    }

    private fun setUpViews() {
        binding.pokemonListFilter.filterFab.setOnClickListener {
            tryShowFiltersAnimation()
        }
    }

    private fun tryHideFiltersAnimation() {
        if (!filterIsExpanding) {
            if (filterIsExpanded) {
                binding.pokemonListFilter.filterHolder.circleHide(this)
            }
        }
    }

    private fun tryShowFiltersAnimation() {
        if (!filterIsExpanding) {
            if (!filterIsExpanded) {
                binding.pokemonListFilter.filterFab.arcAnimateFilterFabIn(
                    binding.pokemonListFilter.filterHolder,
                    this
                )
            }
        }
    }

    private fun setUpFilterView(selections: MutableSet<String>) {
        binding.pokemonListFilter.closeFiltersButton.setOnClickListener {
            tryHideFiltersAnimation()
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
            scrollAwareFilterFabAnimationListener = this
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
            viewLifecycleOwner,
            Observer { filterIsExpanded ->
                if (filterIsExpanded && this.filterIsExpanded) {
                    binding.root.post {
                        binding.pokemonListFilter.filterHolder.circleReveal(null)
                        binding.pokemonListFilter.filterFab.arcAnimateFilterFabIn(
                            binding.pokemonListFilter.filterHolder,
                            null
                        )
                    }
                }
                this.filterIsExpanded = filterIsExpanded
            })
    }

    private fun checkForEmptyLayout(it: List<PokemonWithTypesAndSpecies>) {
        val content = binding.pokemonListFragmentContent
        binding.pokemonListFragmentContent.emptyPokemonList.pokemonListLoading.visibility =
            View.GONE
        if (it.isNotEmpty()) {
            hideRecyclerViewEmptyLayout(content)
        } else {
            showRecyclerViewEmptyLayout(content)
        }
    }

    private fun showRecyclerViewEmptyLayout(content: PokemonListFragmentContentBinding) {
        content.emptyPokemonList.emptyResultsImage.visibility = View.VISIBLE
        content.emptyPokemonList.emptyResultsText.visibility = View.VISIBLE
    }

    private fun hideRecyclerViewEmptyLayout(content: PokemonListFragmentContentBinding) {
        content.emptyPokemonList.emptyResultsImage.visibility = View.GONE
        content.emptyPokemonList.emptyResultsText.visibility = View.GONE
    }

    private fun setUpPokemonRecyclerView(context: Context) {
        binding.pokemonListFragmentContent.pokemonListRecyclerView.run {
            addRecyclerViewDecoration(this, context)
            adapter = pokemonAdapter
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
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
        tryHideFiltersAnimation()
        navigate(action, extras)
    }

    private fun navigate(destination: NavDirections, extraInfo: FragmentNavigator.Extras) =
        with(findNavController()) {
            currentDestination?.getAction(destination.actionId)
                ?.let { navigate(destination, extraInfo) }
        }

    override fun onItemSelected(pokemon: Pokemon, view: View) {
        navigateToDetailFragment(pokemon.name, view)
    }

    override fun onFilterSelected(key: String, value: Boolean) {
        if (value){
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            filterIsExpanded = savedInstanceState.getBoolean(isFiltersLayoutExpandedKey)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isFiltersLayoutExpandedKey, filterIsExpanded)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCircleRevealAnimationFinished() {
//        Last animation called when opening the filters view
        if (!filterIsExpanded) {
            pokemonListViewModel.setFiltersLayoutExpanded(true)
        }
    }

    override fun onCircleHideAnimationFinished() {
//        Used to orchestrate closing filters view animation
        if (filterIsExpanded) {
            binding.pokemonListFilter.filterFab.arcAnimateFilterFabOut(this)
        }
    }

    override fun onArcAnimationFinished() {
//        Last animation called when closing the filters view
//        else used to orchestrate opening filters view animation
        if (!filterIsExpanded) {
            binding.pokemonListFilter.filterHolder.circleReveal(this)
        } else {
            pokemonListViewModel.setFiltersLayoutExpanded(false)
        }
    }

    //Unused animation callbacks
    override fun onSlideHideAnimationStarted() {}
    override fun onSlideHideAnimationFinished() {}
    override fun onCircleRevealAnimationStarted() {}
    override fun onCircleHideAnimationStarted() {}
    override fun onArcAnimationStarted() {}

    companion object {
        private const val isFiltersLayoutExpandedKey: String = "isFiltersLayoutExpanded"
    }
}