package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityMetaData.Companion.createAbilityMetaDataId
import com.sealstudios.pokemonApp.database.`object`.PokemonAbilityWithMetaData
import com.sealstudios.pokemonApp.database.`object`.PokemonWithAbilitiesAndMetaData
import com.sealstudios.pokemonApp.databinding.PokemonAbilityFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonAbilityAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.AdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.layoutManagers.NoScrollLayoutManager
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.util.decorators.PokemonAbilityListDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonAbilityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonAbilityFragment : Fragment(), AdapterClickListener {

    private var pokemonAbilityAdapter: PokemonAbilityAdapter? = null
    private val pokemonAbilityViewModel: PokemonAbilityViewModel by viewModels({ requireParentFragment() })
    private var _binding: PokemonAbilityFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = PokemonAbilityFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPokemonAdapter()
        setUpPokemonAdapterRecyclerView()
        observePokemonAbilities()
    }

    private fun setUpPokemonAdapter() {
        pokemonAbilityAdapter = PokemonAbilityAdapter(this)
    }

    private fun observePokemonAbilities() {
        pokemonAbilityViewModel.pokemonAbilities.observe(
                viewLifecycleOwner,
                Observer { pokemonWithAbilitiesAndMetaDataResource ->
                    when (pokemonWithAbilitiesAndMetaDataResource.status) {
                        Status.SUCCESS -> {
                            if (pokemonWithAbilitiesAndMetaDataResource.data != null) {
                                val pokemonWithAbilitiesAndMetaData = pokemonWithAbilitiesAndMetaDataResource.data
                                val abilitiesWithMetaDataList = mapPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData(pokemonWithAbilitiesAndMetaData)
                                setPokemonAbilities(abilitiesWithMetaDataList)
                            } else {
                                binding.setEmpty()
                            }
                        }
                        Status.ERROR -> {
                            binding.setError(
                                    pokemonWithAbilitiesAndMetaDataResource.message
                                            ?: "Oops, something went wrong..."
                            )
                            { pokemonAbilityViewModel.retry() }
                        }
                        Status.LOADING -> {
                            binding.setLoading()
                        }
                    }
                })
    }

    private fun mapPokemonWithAbilitiesAndMetaDataToAbilitiesWithMetaData(pokemonWithAbilitiesAndMetaData: PokemonWithAbilitiesAndMetaData): List<PokemonAbilityWithMetaData> {
        return pokemonWithAbilitiesAndMetaData.abilities.map { ability ->
            pokemonWithAbilitiesAndMetaData.pokemonAbilityMetaData.filter {
                it.id == createAbilityMetaDataId(
                        pokemonWithAbilitiesAndMetaData.pokemon.id,
                        ability.id
                )
            }.map { PokemonAbilityWithMetaData(ability, it) }
        }.flatten()
    }

    private fun setPokemonAbilities(
            pokemonAbilities: List<PokemonAbilityWithMetaData>
    ) {
        if (pokemonAbilities.isEmpty()) {
            binding.setEmpty()
        } else {
            binding.setNotEmpty()
            pokemonAbilityAdapter?.submitList(pokemonAbilities)
        }
    }

    private fun setUpPokemonAdapterRecyclerView() = binding.pokemonAbilityRecyclerView.apply {
        adapter = pokemonAbilityAdapter
        addPokemonAdapterRecyclerViewDecoration(this)
        layoutManager = NoScrollLayoutManager(context = this.context)
    }

    private fun addPokemonAdapterRecyclerViewDecoration(
            recyclerView: RecyclerView
    ) {
        recyclerView.addItemDecoration(
                PokemonAbilityListDecoration(
                        recyclerView.context.resources.getDimensionPixelSize(
                                R.dimen.qualified_small_margin_8dp
                        ),
                )
        )
    }

    private fun PokemonAbilityFragmentBinding.setLoading() {
        abilityContent.visibility = View.GONE
        abilityError.root.visibility = View.GONE
        abilityLoading.root.visibility = View.VISIBLE
        abilityLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun PokemonAbilityFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        abilityLoading.root.visibility = View.GONE
        abilityContent.visibility = View.GONE
        abilityError.root.visibility = View.VISIBLE
        abilityError.errorImage.visibility = View.GONE
        abilityError.errorText.text = errorMessage
        abilityError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun PokemonAbilityFragmentBinding.setNotEmpty() {
        abilityError.root.visibility = View.GONE
        abilityLoading.root.visibility = View.GONE
        abilityContent.visibility = View.VISIBLE
    }

    private fun PokemonAbilityFragmentBinding.setEmpty() {
        root.visibility = View.GONE
    }

    override fun onItemSelected(position: Int) {
        pokemonAbilityAdapter?.selectItem(position)
    }

}
