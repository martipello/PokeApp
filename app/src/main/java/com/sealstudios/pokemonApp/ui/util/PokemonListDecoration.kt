package com.sealstudios.pokemonApp.ui.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PokemonListDecoration constructor(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val childCount = parent.adapter!!.itemCount

        outRect.left = margin
        outRect.right = margin
        outRect.bottom = margin

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = margin
        }

        if (parent.getChildAdapterPosition(view) == childCount - 1) {
            outRect.bottom = margin * 4
        }


    }
}