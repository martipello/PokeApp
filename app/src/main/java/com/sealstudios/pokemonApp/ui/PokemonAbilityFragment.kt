package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.wrappers.PokemonAbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.PokemonAbilityFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.PokemonAbilityAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.AdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.layoutManagers.NoScrollLayoutManager
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.util.decorators.ListDividerDecoration
import com.sealstudios.pokemonApp.ui.viewModel.PokemonAbilityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PokemonAbilityFragment : Fragment(), AdapterClickListener {

    private var pokemonAbilityAdapter: PokemonAbilityAdapter? = null
    private val pokemonAbilityViewModel: PokemonAbilityViewModel by viewModels({ requireParentFragment() })
    private var _binding: PokemonAbilityFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
                { pokemonWithAbilitiesAndMetaDataResource ->
                    when (pokemonWithAbilitiesAndMetaDataResource.status) {
                        Status.SUCCESS -> {
                            if (pokemonWithAbilitiesAndMetaDataResource.data != null) {
                                setPokemonAbilities(pokemonWithAbilitiesAndMetaDataResource.data)
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
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        setHasFixedSize(true)
        addPokemonAdapterRecyclerViewDecoration(this)
        layoutManager = NoScrollLayoutManager(context = this.context)
    }

    private fun addPokemonAdapterRecyclerViewDecoration(
            recyclerView: RecyclerView
    ) {
        recyclerView.addItemDecoration(
                ListDividerDecoration(R.drawable.divider,
                        binding.root.context,
                        recyclerView.context.resources.getDimensionPixelSize(
                                R.dimen.qualified_small_margin_8dp
                        ))
        )
    }

    override fun onItemSelected(position: Int) {
        pokemonAbilityAdapter?.selectItem(position)
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

}
