package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.database.`object`.PokemonMove
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveWithMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonMoveWithMetaData.Companion.separateByGeneration
import com.sealstudios.pokemonApp.databinding.PokemonMovesFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonMoveAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.PokemonMoveAdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.GenerationHeader
import com.sealstudios.pokemonApp.ui.adapter.helperObjects.PokemonMoveAdapterItem
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.GenerationHeaderViewHolder
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.PokemonMoveViewHolder
import com.sealstudios.pokemonApp.ui.util.PokemonGeneration
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonMoveListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonMovesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PokemonMovesFragment : Fragment(), PokemonMoveAdapterClickListener {

    private var pokemonMoveAdapter: PokemonMoveAdapter? = null
    private var _binding: PokemonMovesFragmentBinding? = null
    private val binding get() = _binding!!
    private val pokemonMovesViewModel: PokemonMovesViewModel by viewModels(ownerProducer = {requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PokemonMovesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPokemonAdapter()
        setUpPokemonMovesRecyclerView()
        observeMoves()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        pokemonMoveAdapter = null
    }


    private fun setUpPokemonAdapter() {
        pokemonMoveAdapter = PokemonMoveAdapter(clickListener = this)
    }

    private fun observeMoves() {
        pokemonMovesViewModel.pokemonMoves.observe(viewLifecycleOwner, Observer { pokemonWithMovesAndMetaData ->
            lifecycleScope.launch(Dispatchers.IO) {
                val movesWithMetaDataList: MutableList<PokemonMoveWithMetaData> = arrayListOf()
                for (move in pokemonWithMovesAndMetaData.moves) {
                    for (metaData in pokemonWithMovesAndMetaData.pokemonMoveMetaData) {
                        if (move.name == metaData.moveName) {
                            movesWithMetaDataList.add(
                                PokemonMoveWithMetaData(
                                    metaData, move
                                )
                            )
                        }
                    }
                }

                setPokemonMoves(movesWithMetaDataList.separateByGeneration())
            }
        })
    }

    private suspend fun setPokemonMoves(
        pokemonMoves: Map<String, List<PokemonMoveWithMetaData>?>
    ) {
        withContext(context = Dispatchers.IO) {
            val pokemonMoveList = pokemonMoveAsync(pokemonMoves = pokemonMoves).await()
            lifecycleScope.launch(Dispatchers.Main){
                pokemonMoveAdapter?.submitList(pokemonMoveList)
            }
            withContext(Dispatchers.Main) {
                binding.pokemonMovesLoading.visibility = View.GONE
                if (pokemonMoveList.isEmpty()) {
                    binding.pokemonMovesEmptyText.visibility = View.VISIBLE
                } else {
                    binding.pokemonMovesEmptyText.visibility = View.GONE
                }
            }
        }
    }

    private suspend fun pokemonMoveAsync(pokemonMoves: Map<String, List<PokemonMoveWithMetaData>?>) = withContext(Dispatchers.IO) {
        return@withContext async {
            val pokemonMoveList = mutableListOf<PokemonMoveAdapterItem>()
            for (moveEntry in pokemonMoves.entries) {
                pokemonMoveList.add(
                    PokemonMoveAdapterItem(
                        moveWithMetaData = null,
                        header = GenerationHeader(headerName = PokemonGeneration.formatGenerationName(
                            PokemonGeneration.getGeneration(moveEntry.key))),
                        itemType = GenerationHeaderViewHolder.layoutType
                    )
                )
                if (!moveEntry.value.isNullOrEmpty()) {
                    pokemonMoveList.addAll(moveEntry.value!!.map {
                        PokemonMoveAdapterItem(
                            moveWithMetaData = it,
                            header = null,
                            itemType = PokemonMoveViewHolder.layoutType
                        )
                    })
                }
            }
            pokemonMoveList
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
}