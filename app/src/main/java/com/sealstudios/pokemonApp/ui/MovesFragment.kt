package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.Move
import com.sealstudios.pokemonApp.databinding.MovesFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.MoveAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.MoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.MoveAdapterItem
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.util.decorators.MoveListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.MovesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MovesFragment : Fragment(), MoveAdapterClickListener {

    private var moveAdapter: MoveAdapter? = null
    private var _binding: MovesFragmentBinding? = null
    private val binding get() = _binding!!
    private val movesViewModel: MovesViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = MovesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPokemonAdapter()
        setUpPokemonMovesRecyclerView()
        observeMoves()
    }

    private fun setUpPokemonAdapter() {
        moveAdapter = MoveAdapter(clickListener = this)
    }

    private fun observeMoves() {
        movesViewModel.moves.observe(viewLifecycleOwner, { pokemonWithMovesAndMetaDataResource ->
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
                    { movesViewModel.retry() }
                }
                Status.LOADING -> {
                    binding.setLoading()
                }
            }
        })
    }

    private suspend fun setPokemonMoves(
            moves: MutableList<MoveAdapterItem>
    ) {
        withContext(context = Dispatchers.IO) {
            moveAdapter?.submitList(moves)
            lifecycleScope.launch(Dispatchers.Main) {
                if (moves.isEmpty()) {
                    binding.setEmpty()
                } else {
                    binding.setNotEmpty()
                }
            }
        }
    }

    private fun setUpPokemonMovesRecyclerView() = binding.pokemonMoveRecyclerView.apply {
        adapter = moveAdapter
        addPokemonMovesRecyclerViewDecoration(this)
       layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun addPokemonMovesRecyclerViewDecoration(
            recyclerView: RecyclerView
    ) {
        recyclerView.addItemDecoration(
                MoveListDecoration(
                        recyclerView.context.resources.getDimensionPixelSize(
                                R.dimen.qualified_small_margin_8dp
                        )
                )
        )
    }

    override fun onItemSelected(position: Int, move: Move) {
        moveAdapter?.selectItem(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        moveAdapter = null
    }

    private fun MovesFragmentBinding.setLoading() {
        movesContent.visibility = View.GONE
        movesError.root.visibility = View.GONE
        binding.movesEmptyText.visibility = View.GONE
        movesLoading.root.visibility = View.VISIBLE
        movesLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun MovesFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
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

    private fun MovesFragmentBinding.setNotEmpty() {
        movesError.root.visibility = View.GONE
        movesLoading.root.visibility = View.GONE
        binding.movesEmptyText.visibility = View.GONE
        movesContent.visibility = View.VISIBLE
    }

    private fun MovesFragmentBinding.setEmpty() {
        movesError.root.visibility = View.GONE
        movesLoading.root.visibility = View.GONE
        movesContent.visibility = View.GONE
        movesEmptyText.visibility = View.VISIBLE
    }

}
