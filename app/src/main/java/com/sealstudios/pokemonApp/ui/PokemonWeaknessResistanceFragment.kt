package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonType
import com.sealstudios.pokemonApp.ui.util.PokemonType as typeEnum
import com.sealstudios.pokemonApp.databinding.PokemonWeaknessResistanceFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonWeaknessResistanceAdapter
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.util.TypesAndCategoryGroupHelper
import com.sealstudios.pokemonApp.ui.viewModel.PokemonWeaknessResistanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PokemonWeaknessResistanceFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private var _binding: PokemonWeaknessResistanceFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var pokemonWeaknessResistanceAdapter: PokemonWeaknessResistanceAdapter

    private val pokemonWeaknessResistanceViewModel: PokemonWeaknessResistanceViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = PokemonWeaknessResistanceFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pokemonWeaknessResistanceAdapter = PokemonWeaknessResistanceAdapter(glide)
        setUpRecyclerView()
        observePokemonTypeWeaknessResistance()
    }

    private fun observePokemonTypeWeaknessResistance() {
        pokemonWeaknessResistanceViewModel.pokemonWeaknessAndResistance.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null) {
                        if (it.data.types.any { type -> type.doubleDamageFrom.isEmpty() }) {
                            //Can't be confident to have accurate data if any of these are empty
                            binding.setEmpty()
                        } else {
                            setAdapterData(it.data.types)
                            binding.setNotEmpty()
                        }
                    } else {
                        binding.setNotEmpty()
                    }
                }
                Status.ERROR -> {
                    binding.setError(it.message ?: "Oops, something went wrong...") {
                        pokemonWeaknessResistanceViewModel.retry()
                    }
                }
                Status.LOADING -> binding.setLoading()
            }
        })
    }

    private fun setAdapterData(types: List<PokemonType>) {
        val pokemonEnumTypes = typeEnum.getPokemonEnumTypesForPokemonTypes(types)
        val weaknessResistanceList = typeEnum.getAllPokemonTypes()
        // TODO: work out weakness and resistance
        val pairList = listOf<Pair<String,String>>()

        pokemonWeaknessResistanceAdapter.submitList(pairList)
    }

    private fun setUpRecyclerView() = with(binding.weaknessResistanceGrid) {
        adapter = pokemonWeaknessResistanceAdapter
        layoutManager = GridLayoutManager(this.context, 3, GridLayoutManager.VERTICAL, false)
    }

    private fun PokemonWeaknessResistanceFragmentBinding.setLoading() {
        weaknessResistanceContent.visibility = View.GONE
        weaknessResistanceError.root.visibility = View.GONE
        weaknessResistanceLoading.root.visibility = View.VISIBLE
        weaknessResistanceLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonWeaknessResistanceFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        weaknessResistanceLoading.root.visibility = View.GONE
        weaknessResistanceContent.visibility = View.GONE
        weaknessResistanceError.root.visibility = View.VISIBLE
        weaknessResistanceError.errorImage.visibility = View.GONE
        weaknessResistanceError.errorText.text = errorMessage
        weaknessResistanceError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonWeaknessResistanceFragmentBinding.setNotEmpty() {
        weaknessResistanceError.root.visibility = View.GONE
        weaknessResistanceLoading.root.visibility = View.GONE
        weaknessResistanceContent.visibility = View.VISIBLE
    }

    private fun PokemonWeaknessResistanceFragmentBinding.setEmpty() {
        root.visibility = View.GONE
    }

}