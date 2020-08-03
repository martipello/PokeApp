package com.sealstudios.pokemonApp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sealstudios.pokemonApp.objects.Pokemon
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonListAdapter
import com.sealstudios.pokemonApp.ui.viewModels.PokemonViewModel

class PokemonListFragment : Fragment() {

    private var _binding: PokemonListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var pokemonViewModel: PokemonViewModel
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
        pokemonListAdapter = PokemonListAdapter(context = view.context)
        setUpPokemonRecyclerView(view.context)
        pokemonViewModel = ViewModelProvider(this).get(PokemonViewModel::class.java)
        observePokemonList()
    }

    private fun observePokemonList() {
        pokemonViewModel.allPokemon.observe(viewLifecycleOwner, Observer { pokemonList ->
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

    private fun navigateToDetailFragment() {
        NavHostFragment.findNavController(this@PokemonListFragment)
                .navigate(R.id.action_FirstFragment_to_SecondFragment)
    }
}