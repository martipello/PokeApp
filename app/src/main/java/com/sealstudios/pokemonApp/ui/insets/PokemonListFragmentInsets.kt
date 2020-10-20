package com.sealstudios.pokemonApp.ui.insets

import androidx.core.view.updatePadding
import com.sealstudios.pokemonApp.R
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding

class PokemonListFragmentInsets {

    fun setInsets(binding: PokemonListFragmentBinding) {
        val context = binding.root.context
        val appBarLayout = binding.pokemonListFragmentCollapsingAppBar
        val filterLayout = binding.pokemonListFilter
        val listLayout = binding.pokemonListFragmentContent

        appBarLayout.appBarLayout.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            view.layoutParams = marginLayoutParams
        }

        appBarLayout.toolbar.doOnApplyWindowInsetPadding { view, windowInsets, initialPadding ->
            view.updatePadding(
                left = windowInsets.systemWindowInsetLeft + initialPadding.left,
                right = windowInsets.systemWindowInsetRight + initialPadding.right
            )
        }

        appBarLayout.toolbarLayout.doOnApplyWindowInsetPadding { _, _, _ ->
            //required or the views below do not get there padding updated
        }

        listLayout.pokemonListRecyclerView.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                right = windowInsets.systemWindowInsetRight,
                left = windowInsets.systemWindowInsetLeft,
                bottom = windowInsets.systemWindowInsetBottom
            )
        }

        filterLayout.filterGroupLayout.chipGroup.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                bottom = windowInsets.systemWindowInsetBottom,
                right = windowInsets.systemWindowInsetRight,
                left = windowInsets.systemWindowInsetLeft
            )
        }

        filterLayout.filterPokemonLabel.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                left = context.resources.getDimension(R.dimen.qualified_medium_margin_16dp)
                    .toInt() + windowInsets.systemWindowInsetLeft
            )
        }

        filterLayout.closeFiltersButton.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                right = windowInsets.systemWindowInsetRight
            )
        }

        filterLayout.filterFab.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
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