package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.MainActivity
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.Pokemon
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.ClickListener
import com.sealstudios.pokemonApp.ui.adapter.PokemonAdapter
import com.sealstudios.pokemonApp.ui.util.PokemonListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonDetailViewModel
import com.sealstudios.pokemonApp.ui.viewModel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PokemonListFragment : Fragment(), ClickListener {

    @Inject
    lateinit var glide: RequestManager
    private var _binding: PokemonListFragmentBinding? = null
    private val binding get() = _binding!!
    private val pokemonListViewModel: PokemonListViewModel by viewModels()
    private val pokemonDetailViewModel: PokemonDetailViewModel
            by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpActionBar()
        setUpPokemonAdapter()
        setUpPokemonRecyclerView(view.context)
        observePokemonList()
    }

    private fun setUpActionBar() {
        val navController = NavHostFragment.findNavController(this@PokemonListFragment)
        NavigationUI.setupActionBarWithNavController(activity as MainActivity, navController)
    }

    private fun setUpPokemonAdapter() {
        pokemonAdapter = PokemonAdapter(clickListener = this, glide = glide)
    }

    private fun observePokemonList() {
        pokemonListViewModel.searchPokemon.observe(viewLifecycleOwner, Observer { pokemonList ->
            pokemonList?.let {
                if (it.isNotEmpty()) {
                    pokemonAdapter.submitList(it)
                    binding.pokemonListLoading.visibility = View.GONE
                }
            }
        })
    }

    private fun setUpPokemonRecyclerView(context: Context) {
        binding.pokemonListRecyclerView.addItemDecoration(PokemonListDecoration(context.resources.getDimensionPixelSize(R.dimen.small_margin_8dp)))
        binding.pokemonListRecyclerView.adapter = pokemonAdapter
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

    private fun navigateToDetailFragment() {
        NavHostFragment.findNavController(this@PokemonListFragment)
            .navigate(R.id.action_PokemonListFragment_to_PokemonDetailFragment)
    }

    override fun onItemSelected(position: Int, item: Pokemon) {
        pokemonDetailViewModel.setSearch(item.id)
        navigateToDetailFragment()
    }

}