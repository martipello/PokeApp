package com.sealstudios.pokemonApp.ui.insets

import androidx.core.view.updatePadding
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentFilterHolderBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding

class PokemonFilterFragmentInsets {

    fun setInsets(binding: PokemonListFragmentFilterHolderBinding) {
        val context = binding.root.context

        binding.filterGroupLayout.chipGroup.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                bottom = windowInsets.systemWindowInsetBottom,
                right = windowInsets.systemWindowInsetRight,
                left = windowInsets.systemWindowInsetLeft
            )
        }

        binding.filterPokemonLabel.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                left = context.resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetLeft
            )
        }

        binding.closeFiltersButton.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                right = windowInsets.systemWindowInsetRight
            )
        }

        binding.filterFab.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.bottomMargin =
                context.resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetBottom
            marginLayoutParams.rightMargin =
                context.resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetRight
            view.layoutParams = marginLayoutParams
        }

    }

}