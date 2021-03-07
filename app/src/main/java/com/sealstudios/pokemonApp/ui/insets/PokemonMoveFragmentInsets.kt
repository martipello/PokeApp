package com.sealstudios.pokemonApp.ui.insets

import androidx.core.view.updatePadding
import com.sealstudios.pokemonApp.databinding.PokemonListFragmentBinding
import com.sealstudios.pokemonApp.databinding.PokemonMovesFragmentBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetMargin
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding

class PokemonMoveFragmentInsets {

    fun setInsets(binding: PokemonMovesFragmentBinding) {
        binding.pokemonMoveRecyclerView.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                bottom = windowInsets.systemWindowInsetBottom
            )
        }

    }

}