package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.insets.PokemonListFragmentInsets
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonFiltersViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import com.sealstudios.pokemonApp.ui.viewModel.RemotePokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class PokemonListFragment : Fragment(),
        PokemonAdapterClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var ads: MutableList<PokemonAdapterListItem> = mutableListOf()

    @Inject
    lateinit var glide: RequestManager

    private val binding get() = _binding!!
    private var _binding: PokemonListFragmentBinding? = null

    private var search: String = ""
    private val pokemonListViewModel: PokemonListViewModel by viewModels({ requireActivity() })
    private val pokemonFiltersViewModel: PokemonFiltersViewModel by viewModels({ requireActivity() })
    private val remotePokemonViewModel: RemotePokemonViewModel by viewModels()
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = PokemonListFragmentBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setActionBar()
        PokemonListFragmentInsets().setInsets(binding)
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpPokemonAdapter()
        setUpSwipeRefresh()
        setUpPokemonRecyclerView(view.context)
        observeFetchAllPokemonResponse()
        observeSearch()
        lifecycleScope.launch {
            createAds(view)
        }
    }

    private suspend fun createAds(view: View) {
        withContext(Dispatchers.IO) {
            val adsManager = AdsManager(view.context)
            adsManager.createAds(listener = UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {
                ads.add(MyNativeAd(it))
                if (!adsManager.getAdLoader().isLoading) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        pokemonListViewModel.refresh()
                    }
                }
                if (activity?.isDestroyed == true || activity?.isFinishing == true || activity?.isChangingConfigurations == true) {
                    it.destroy()
                    return@OnUnifiedNativeAdLoadedListener
                }
            })
        }

    }

    private fun setUpPokemonAdapter() {
        pokemonAdapter = PokemonAdapter(clickListener = this, glide = glide)
    }

    private fun observeFetchAllPokemonResponse() {
        remotePokemonViewModel.allPokemonResponse.observe(viewLifecycleOwner, { allPokemon ->
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
                viewLifecycleOwner, { pokemonData ->
            if (pokemonData != null) {
                if (pokemonData.isEmpty()) {
                    binding.setEmpty()
                } else {
                    binding.setNotEmpty()
                }
                if (ads.isNotEmpty()) {
                    lifecycleScope.launch {
                        pokemonAdapter.submitList(mergeAds(pokemonData, ads))
                    }
                } else {
                    pokemonAdapter.submitList(pokemonData)
                }
            } else {
                binding.setLoading()
            }
        })
    }

    private suspend fun mergeAds(
            pokemon: List<PokemonAdapterListItem>,
            ads: List<PokemonAdapterListItem>
    ): List<PokemonAdapterListItem> {
        return withContext(Dispatchers.Default) {
            val mergedList = mutableListOf<PokemonAdapterListItem>()
            mergedList.addAll(pokemon)
            val intersect = pokemon.size / ads.size
            var counter = 0
            for (index in pokemon.indices) {
                if (index % intersect == 0) {
                    counter++
                    if (counter < ads.size) {
                        mergedList.add(index, ads[counter])
                    }
                }
            }
            return@withContext mergedList
        }
    }

    private fun observeSearch() {
        pokemonListViewModel.search.observe(viewLifecycleOwner, {
            if (it != null) {
                search = it.replace("%", "")
            }
        })
    }

    private fun setUpSwipeRefresh() {
        binding.pokemonListFragmentContent.swipeRefreshPokemonList
                .setProgressBackgroundColorSchemeColor(
                        ContextCompat.getColor(binding.root.context, R.color.primaryLightColor))
        binding.pokemonListFragmentContent.swipeRefreshPokemonList
                .setColorSchemeResources(R.color.darkIconColor)
        binding.pokemonListFragmentContent.swipeRefreshPokemonList.setOnRefreshListener(this@PokemonListFragment)
    }

    private fun setUpPokemonRecyclerView(context: Context) {
        binding.pokemonListFragmentContent.pokemonListRecyclerView.run {
            addItemDecoration(PokemonListDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.qualified_small_margin_8dp)))
            this.setHasFixedSize(true)
            adapter = pokemonAdapter
            addItemDecoration(PokemonListDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.qualified_small_margin_8dp)))
            doOnPreDraw {
                startPostponedEnterTransition()
            }
            pokemonFiltersViewModel.addScrollAwareFilerFab(this)
        }
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
        setToolbarTitleColor(toolbar.context)
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

    private fun setToolbarTitleColor(context: Context) {
        binding.pokemonListFragmentCollapsingAppBar.toolbarLayout
                .setExpandedTitleColor(
                        ContextCompat.getColor(
                                context,
                                android.R.color.transparent
                        )
                )
        binding.pokemonListFragmentCollapsingAppBar.toolbarLayout
                .setCollapsedTitleTextColor(ContextCompat.getColor(
                        context,
                        android.R.color.white
                ))
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
                transitionName = view.transitionName)
        val extras = FragmentNavigatorExtras(
                view to view.transitionName
        )
        pokemonFiltersViewModel.closeFiltersLayout()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                navigate(PokemonListFragmentDirections.actionPokemonListFragmentToPreferences(), FragmentNavigatorExtras())
                return true
            }
        }
        return false
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

}


