package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.databinding.PokemonMovesFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonMoveAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveAdapterItem
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonMoveListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonMovesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PokemonMovesFragment : Fragment(), PokemonMoveAdapterClickListener {

    private var pokemonMoveAdapter: PokemonMoveAdapter? = null
    private var _binding: PokemonMovesFragmentBinding? = null
    private val binding get() = _binding!!
    private val pokemonMovesViewModel: PokemonMovesViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = PokemonMovesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPokemonAdapter()
        setUpPokemonMovesRecyclerView()
        observeMoves()
    }

    private fun setUpPokemonAdapter() {
        pokemonMoveAdapter = PokemonMoveAdapter(clickListener = this)
    }

    private fun observeMoves() {
        pokemonMovesViewModel.pokemonMoves.observe(viewLifecycleOwner, { pokemonWithMovesAndMetaDataResource ->
            Log.d("MOVE_FRAG", "STATUS ${pokemonWithMovesAndMetaDataResource.status}")
            when (pokemonWithMovesAndMetaDataResource.status) {
                Status.SUCCESS -> {
                    if (pokemonWithMovesAndMetaDataResource.data != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            setPokemonMoves(pokemonWithMovesAndMetaDataResource.data)
                        }
                    } else {
                        binding.setEmpty()
                    }
                }
                Status.ERROR -> {
                    binding.setError(pokemonWithMovesAndMetaDataResource.message
                            ?: "Oops, something went wrong...")
                    { pokemonMovesViewModel.retry() }
                }
                Status.LOADING -> {
                    binding.setLoading()
                }
            }
        })
    }

    private suspend fun setPokemonMoves(
            pokemonMoves: MutableList<PokemonMoveAdapterItem>
    ) {
        withContext(context = Dispatchers.IO) {
            pokemonMoveAdapter?.submitList(pokemonMoves)
            lifecycleScope.launch(Dispatchers.Main) {
                if (pokemonMoves.isEmpty()) {
                    binding.setEmpty()
                } else {
                    binding.setNotEmpty()
                }
            }
        }
    }

    private fun setUpPokemonMovesRecyclerView() = binding.pokemonMoveRecyclerView.apply {
        adapter = pokemonMoveAdapter
        addPokemonMovesRecyclerViewDecoration(this)
    }

    private fun addPokemonMovesRecyclerViewDecoration(
            recyclerView: RecyclerView
    ) {
        recyclerView.addItemDecoration(
                PokemonMoveListDecoration(
                        recyclerView.context.resources.getDimensionPixelSize(
                                R.dimen.qualified_small_margin_8dp
                        )
                )
        )
    }

    override fun onItemSelected(position: Int, pokemonMove: PokemonMove) {
        pokemonMoveAdapter?.selectItem(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        pokemonMoveAdapter = null
    }

    private fun PokemonMovesFragmentBinding.setLoading() {
        movesContent.visibility = View.GONE
        movesError.root.visibility = View.GONE
        binding.movesEmptyText.visibility = View.GONE
        movesLoading.root.visibility = View.VISIBLE
        movesLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonMovesFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        movesLoading.root.visibility = View.GONE
        movesContent.visibility = View.GONE
        binding.movesEmptyText.visibility = View.GONE
        movesError.errorImage.visibility = View.GONE
        movesError.root.visibility = View.VISIBLE
        movesError.errorText.text = errorMessage
        movesError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonMovesFragmentBinding.setNotEmpty() {
        movesError.root.visibility = View.GONE
        movesLoading.root.visibility = View.GONE
        binding.movesEmptyText.visibility = View.GONE
        movesContent.visibility = View.VISIBLE
    }

    private fun PokemonMovesFragmentBinding.setEmpty() {
        movesError.root.visibility = View.GONE
        movesLoading.root.visibility = View.GONE
        movesContent.visibility = View.GONE
        movesEmptyText.visibility = View.VISIBLE
    }

}
