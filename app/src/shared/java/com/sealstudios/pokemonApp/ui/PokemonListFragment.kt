package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.database.`object`.MyNativeAd
import com.sealstudios.pokemonApp.database.`object`.PokemonForList
import com.sealstudios.pokemonApp.database.`object`.objectInterface.PokemonAdapterListItem
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPokemonDetailFragment
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPreferences
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonAdapterClickListener
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.insets.PokemonListFragmentInsets
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PartialPokemonViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonFiltersViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
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
    private val partialPokemonViewModel: PartialPokemonViewModel by viewModels()

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
        lifecycleScope.launch {
            createAds(view)
        }
        setHasOptionsMenu(true)
        setUpPokemonAdapter()
        setUpSwipeRefresh()
        setUpPokemonRecyclerView()
        observeRequestDownloadDataPermission()
        onFetchedPartialPokemonData()
        observeSearch()
    }

    private fun setUpPokemonAdapter() {
        pokemonAdapter = PokemonAdapter(clickListener = this, glide = glide)
    }

    private fun onFetchedPartialPokemonData() {
        partialPokemonViewModel.onFetchedPartialPokemonData.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> observePokemonList()
                Status.ERROR -> {
                    binding.setError(it.message ?: getString(R.string.error_text)) {
                        partialPokemonViewModel.retryGetPartialPokemonEvent()
                    }
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun observeRequestDownloadDataPermission() {
        partialPokemonViewModel.requestDownloadPermission.observe(viewLifecycleOwner, {
            DownloadRequestDialog().show(parentFragmentManager, DownloadRequestDialog.TAG)
        })
    }

    private fun observePokemonList() {
        lifecycleScope.launch {
            pokemonListViewModel.searchPokemon.collectLatest { pokemonData ->
                if (pokemonData != null) {
                    if (pokemonData.isEmpty()) {
                        binding.setEmpty()
                    } else {
                        pokemonAdapter.submitList(mergeAds(pokemonData, ads))
                        binding.setNotEmpty()
                    }
                } else {
                    binding.setLoading()
                }
            }
        }
    }

    private fun observeSearch() {
        pokemonListViewModel.searchState.observe(viewLifecycleOwner, {
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

    private fun setUpPokemonRecyclerView() {
        binding.pokemonListFragmentContent.pokemonListRecyclerView.run {
            setHasFixedSize(true)
            adapter = pokemonAdapter
            addItemDecoration(PokemonListDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.qualified_small_margin_8dp)))
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
        setToolbarImage()
    }

    private fun setToolbarImage() {
        glide.load(R.drawable.pokemon_logo_black).into(binding.pokemonListFragmentCollapsingAppBar.toolbarImage)
        binding.pokemonListFragmentCollapsingAppBar.toolbarImage.setOnClickListener {
            val intent = Intent()
            intent.action = NotificationHelper.NOTIFICATION_ACTION_KEY
            intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            it.context.sendBroadcast(intent)
        }
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
                    pokemonListViewModel.search(it)
                }
                return false
            }
        })
    }

    private fun navigateToDetailFragment(name: String, view: View) {
        view as MaterialCardView
        val action = actionPokemonListFragmentToPokemonDetailFragment(
                pokemonName = name,
                transitionName = view.transitionName
        )
        val extras = FragmentNavigatorExtras(view to view.transitionName
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
                navigate(actionPokemonListFragmentToPreferences(), FragmentNavigatorExtras())
                return true
            }
        }
        return false
    }

    override fun onRefresh() {
        partialPokemonViewModel.retryGetPartialPokemonEvent()
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
        emptyPokemonList.pokemonListLoading.loading.applyLoopingAnimatedVectorDrawable(
                R.drawable.colored_pokeball_anim_faster,
        )
        emptyPokemonList.pokemonListLoading.root.visibility = View.VISIBLE
        hideEmptyLayout()
        hideErrorLayout()
    }

    private fun PokemonListFragmentBinding.setError(errorMessage: String, fetchAllPokemon: () -> Unit) {
        pokemonListFragmentContent.swipeRefreshPokemonList.isRefreshing = false
        hideEmptyLayout()
        hideLoadingLayout()
        pokemonListFragmentContent.pokemonListRecyclerView.visibility = View.INVISIBLE
        glide.load(R.drawable.pika_detective).into(errorPokemonList.errorImage)
        errorPokemonList.root.visibility = View.VISIBLE
        errorPokemonList.errorImage.visibility = View.VISIBLE
        errorPokemonList.errorText.text = errorMessage
        errorPokemonList.retryButton.setOnClickListener {
            fetchAllPokemon()
        }
    }

    private fun PokemonListFragmentBinding.setEmpty() {
        pokemonListFragmentContent.swipeRefreshPokemonList.isRefreshing = false
        glide.load(R.drawable.no_results_snorlax).into(emptyPokemonList.emptyResultsImage)
        hideErrorLayout()
        hideLoadingLayout()
        pokemonListFragmentContent.pokemonListRecyclerView.visibility = View.INVISIBLE
        emptyPokemonList.emptyResultsImage.visibility = View.VISIBLE
        emptyPokemonList.emptyResultsText.visibility = View.VISIBLE
    }

    private fun PokemonListFragmentBinding.setNotEmpty() {
        pokemonListFragmentContent.pokemonListRecyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
        pokemonListFragmentContent.swipeRefreshPokemonList.isRefreshing = false
        pokemonListFragmentContent.pokemonListRecyclerView.visibility = View.VISIBLE
        hideEmptyLayout()
        hideErrorLayout()
        hideLoadingLayout()
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

    private suspend fun mergeAds(
            pokemon: List<PokemonAdapterListItem>,
            ads: List<PokemonAdapterListItem>
    ): List<PokemonAdapterListItem> {
        return withContext(Dispatchers.Default) {
            val mergedList = mutableListOf<PokemonAdapterListItem>()
            mergedList.addAll(pokemon)
            try {
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
            } catch (e: Exception) {
                Log.d("Exception", "Exception $e")
            }
            return@withContext mergedList
        }
    }

}


