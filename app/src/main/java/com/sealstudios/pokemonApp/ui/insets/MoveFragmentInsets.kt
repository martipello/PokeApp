package com.sealstudios.pokemonApp.ui.insets

import androidx.core.view.updatePadding
import com.sealstudios.pokemonApp.databinding.MovesFragmentBinding
import com.sealstudios.pokemonApp.ui.util.doOnApplyWindowInsetPadding

class MoveFragmentInsets {

    fun setInsets(binding: MovesFragmentBinding) {
        binding.pokemonMoveRecyclerView.doOnApplyWindowInsetPadding { view, windowInsets, _ ->
            view.updatePadding(
                    bottom = windowInsets.systemWindowInsetBottom
            )
        }

    }

}