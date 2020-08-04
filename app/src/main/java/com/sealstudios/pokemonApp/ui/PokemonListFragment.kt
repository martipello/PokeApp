package com.sealstudios.pokemonApp.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.objects.Pokemon
import com.sealstudios.pokemonApp.ui.adapter.PokemonListAdapter
import com.sealstudios.pokemonApp.ui.viewModels.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private var _binding: PokemonListFragmentBinding? = null
    private val binding get() = _binding!!
    private val pokemonViewModel: PokemonViewModel by viewModels()
    private lateinit var pokemonListAdapter: PokemonListAdapter

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
        pokemonListAdapter = PokemonListAdapter(context = view.context)
        setUpPokemonRecyclerView(view.context)
        observePokemonList()
    }

    private fun observePokemonList() {
        pokemonViewModel.searchPokemon.observe(viewLifecycleOwner, Observer { pokemonList ->
            pokemonList?.let { pokemonListAdapter.refreshPokemon(it) }
        })
    }

    private fun setUpPokemonRecyclerView(context: Context) {
        binding.pokemonListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        binding.pokemonListRecyclerView.adapter = pokemonListAdapter
        binding.fab.setOnClickListener{
            pokemonViewModel.insert(Pokemon(id = 3, name = "farfetched"))
        }
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
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                 newText?.let {
                     pokemonViewModel.setSearch("%$it%")
                 }
                return false
            }
        })
    }

    private fun navigateToDetailFragment() {
        NavHostFragment.findNavController(this@PokemonListFragment)
                .navigate(R.id.action_FirstFragment_to_SecondFragment)
    }
}