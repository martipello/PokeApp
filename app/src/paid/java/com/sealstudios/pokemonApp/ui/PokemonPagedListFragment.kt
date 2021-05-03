package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
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
import androidx.paging.ExperimentalPagingApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.card.MaterialCardView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.notification.NotificationHelper
import com.sealstudios.pokemonApp.database.`object`.PokemonGraphQL
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.paging.PokemonGraphQLPagingAdapter
import com.sealstudios.pokemonApp.paging.PokemonPagingAdapterClickListener
import com.sealstudios.pokemonApp.paging.PokemonPagingViewModel
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToDetailFragment
import com.sealstudios.pokemonApp.ui.PokemonListFragmentDirections.Companion.actionPokemonListFragmentToPreferences
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.insets.PokemonListFragmentInsets
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.FiltersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@FlowPreview
@AndroidEntryPoint
class PokemonPagedListFragment : Fragment(),
    PokemonPagingAdapterClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var glide: RequestManager

    private val binding get() = _binding!!
    private var _binding: PokemonListFragmentBinding? = null

    private var search: String = ""

    @ExperimentalPagingApi
    private val pokemonListViewModel: PokemonPagingViewModel by viewModels({ requireActivity() })
    private val filtersViewModel: FiltersViewModel by viewModels({ requireActivity() })

    private lateinit var pokemonAdapter: PokemonGraphQLPagingAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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
        setUpPokemonRecyclerView()
        observeSearch()
    }

    private fun setUpPokemonAdapter() {
        pokemonAdapter = PokemonGraphQLPagingAdapter(clickListener = this, glide = glide)
//        pokemonAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun observePokemonList() {
        lifecycleScope.launch {
            pokemonListViewModel.searchPokemon.collectLatest { pokemonData ->
                pokemonAdapter.submitData(pokemonData)
                if (pokemonAdapter.itemCount > 0){
                    binding.setNotEmpty()
                } else {
                    binding.setEmpty()
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
        binding.pokemonListFragmentContent.swipeRefreshPokemonList.setOnRefreshListener(this@PokemonPagedListFragment)
    }

    private fun setUpPokemonRecyclerView() {
        binding.pokemonListFragmentContent.pokemonListRecyclerView.run {
            setHasFixedSize(true)
            adapter = pokemonAdapter
            addItemDecoration(PokemonListDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.qualified_small_margin_8dp)))
            filtersViewModel.addScrollAwareFilerFab(this)
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
                findNavController(this@PokemonPagedListFragment),
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
        val action = actionPokemonListFragmentToDetailFragment(
                pokemonName = name,
                transitionName = view.transitionName,
        )
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        filtersViewModel.closeFiltersLayout()
        navigate(action, extras)
    }

    private fun navigate(destination: NavDirections, extraInfo: FragmentNavigator.Extras) =
            with(findNavController()) {
                currentDestination?.getAction(destination.actionId)
                        ?.let { navigate(destination, extraInfo) }
            }

    override fun onItemSelected(pokemon: PokemonGraphQL, view: View) {
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
//        partialPokemonViewModel.retryGetPartialPokemonEvent()
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

}