package com.sealstudios.pokemonApp.ui.insets

import androidx.core.view.updatePadding
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding

class PokemonListFragmentInsets {

    fun setInsets(binding: PokemonListFragmentBinding) {
        val appBarLayout = binding.pokemonListFragmentCollapsingAppBar
        val listLayout = binding.pokemonListFragmentContent

        appBarLayout.appBarLayout.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            marginLayoutParams.leftMargin = windowInsets.systemWindowInsetLeft
            marginLayoutParams.rightMargin = windowInsets.systemWindowInsetRight
            view.layoutParams = marginLayoutParams
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

    }

}