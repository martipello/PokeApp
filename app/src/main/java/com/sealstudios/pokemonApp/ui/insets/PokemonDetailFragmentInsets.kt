package com.sealstudios.pokemonApp.ui.insets

import androidx.core.view.updatePadding
import com.sealstudios.pokemonApp.databinding.PokemonDetailFragmentBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding

class PokemonDetailFragmentInsets {

    fun setInsets(binding: PokemonDetailFragmentBinding) {
        binding.appBarLayout.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.topMargin = windowInsets.systemWindowInsetTop
            view.layoutParams = marginLayoutParams
        }
        binding.toolbar.doOnApplyWindowInsetPadding { view, windowInsets, initialPadding ->
            view.updatePadding(
                left = windowInsets.systemWindowInsetLeft + initialPadding.left,
                right = windowInsets.systemWindowInsetRight + initialPadding.right
            )
        }
        binding.toolbarLayout.doOnApplyWindowInsetPadding { _, _, _ ->
            //required or the views below do not get there padding updated
        }
        binding.detailRoot.doOnApplyWindowInsetPadding { _, _, _ ->
            //required or the views below do not get there padding updated
        }
        binding.scrollView.doOnApplyWindowInsetPadding { view, windowInsets, initialPadding ->
            view.updatePadding(
                bottom = windowInsets.systemWindowInsetBottom + initialPadding.bottom
            )
        }

        binding.scrollView.doOnApplyWindowInsetMargin { view, windowInsets, marginLayoutParams ->
            marginLayoutParams.leftMargin = windowInsets.systemWindowInsetLeft
            marginLayoutParams.rightMargin = windowInsets.systemWindowInsetRight
            view.layoutParams = marginLayoutParams
        }
    }

}