package com.sealstudios.pokemonApp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.RequestManager
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.api.`object`.Status
import com.sealstudios.pokemonApp.database.`object`.wrappers.AbilityWithMetaData
import com.sealstudios.pokemonApp.databinding.AbilityFragmentBinding
import com.sealstudios.pokemonApp.ui.adapter.AbilityAdapter
import com.sealstudios.pokemonApp.ui.adapter.clickListeners.AdapterClickListener
import com.sealstudios.pokemonApp.ui.adapter.layoutManagers.NoScrollLayoutManager
import com.sealstudios.pokemonApp.ui.extensions.applyLoopingAnimatedVectorDrawable
import com.sealstudios.pokemonApp.ui.extensions.removeItemDecorations
import com.sealstudios.pokemonApp.ui.util.decorators.ListDividerDecoration
import com.sealstudios.pokemonApp.ui.viewModel.AbilityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AbilityFragment : Fragment(), AdapterClickListener {

    @Inject
    lateinit var glide: RequestManager

    private var abilityAdapter: AbilityAdapter? = null
    private val abilityViewModel: AbilityViewModel by viewModels({ requireParentFragment() })
    private var _binding: AbilityFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AbilityFragmentBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setLoading()
        setUpPokemonAdapter()
        setUpPokemonAdapterRecyclerView()
        observePokemonAbilities()
    }

    private fun setUpPokemonAdapter() {
        abilityAdapter = AbilityAdapter(this)
    }

    private fun observePokemonAbilities() {
        abilityViewModel.abilities.observe(
                viewLifecycleOwner,
                { pokemonWithAbilitiesAndMetaDataResource ->
                    Log.d("ABILITY", "ABILITY DATA ${pokemonWithAbilitiesAndMetaDataResource.data}")
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
                            { abilityViewModel.retry() }
                        }
                        Status.LOADING -> {
                            binding.setLoading()
                        }
                    }
                })
    }

    private fun setPokemonAbilities(
            abilities: List<AbilityWithMetaData>
    ) {
        if (abilities.isEmpty()) {
            binding.setEmpty()
        } else {
            binding.setNotEmpty()
            abilityAdapter?.submitList(abilities)
        }
    }

    private fun setUpPokemonAdapterRecyclerView() = binding.pokemonAbilityRecyclerView.apply {
        adapter = abilityAdapter
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//        setHasFixedSize(true)
        addPokemonAdapterRecyclerViewDecoration(this)
        layoutManager = NoScrollLayoutManager(context = this.context)
    }

    private fun addPokemonAdapterRecyclerViewDecoration(
            recyclerView: RecyclerView
    ) {
        recyclerView.removeItemDecorations()
        recyclerView.addItemDecoration(
                ListDividerDecoration(R.drawable.divider,
                        binding.root.context,
                        recyclerView.context.resources.getDimensionPixelSize(
                                R.dimen.qualified_small_margin_8dp
                        ))
        )
    }

    override fun onItemSelected(position: Int) {
        abilityAdapter?.selectItem(position)
    }

    private fun AbilityFragmentBinding.setLoading() {
        abilityContent.visibility = View.GONE
        abilityError.root.visibility = View.GONE
        emptyAbilitiesList.root.visibility = View.GONE
        abilityLoading.root.visibility = View.VISIBLE
        abilityLoading.loading.applyLoopingAnimatedVectorDrawable(R.drawable.colored_pokeball_anim_faster)
    }

    private fun AbilityFragmentBinding.setError(errorMessage: String, retry: () -> Unit) {
        abilityLoading.root.visibility = View.GONE
        abilityContent.visibility = View.GONE
        abilityError.errorImage.visibility = View.GONE
        emptyAbilitiesList.root.visibility = View.GONE
        abilityError.root.visibility = View.VISIBLE
        abilityError.errorText.text = errorMessage
        abilityError.retryButton.setOnClickListener {
            retry()
        }
    }

    private fun AbilityFragmentBinding.setNotEmpty() {
        abilityError.root.visibility = View.GONE
        abilityLoading.root.visibility = View.GONE
        emptyAbilitiesList.root.visibility = View.GONE
        abilityContent.visibility = View.VISIBLE
    }

    private fun AbilityFragmentBinding.setEmpty() {
        abilityLoading.root.visibility = View.GONE
        abilityContent.visibility = View.GONE
        abilityError.errorImage.visibility = View.GONE
        emptyAbilitiesList.root.visibility = View.GONE
        emptyAbilitiesList.root.visibility = View.VISIBLE
        emptyAbilitiesList.emptyResultsText.text = context?.getString(R.string.abilities_empty)
        glide.load(R.drawable.no_results_snorlax).into(emptyAbilitiesList.emptyResultsImage)
    }

}
