package com.sealstudios.pokemonApp.ui.util.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PokemonMoveListDecoration constructor(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = margin
        }

        outRect.bottom = margin


    }
}