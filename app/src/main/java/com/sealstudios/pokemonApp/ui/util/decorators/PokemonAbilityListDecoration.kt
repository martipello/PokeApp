package com.sealstudios.pokemonApp.ui.util.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class PokemonAbilityListDecoration constructor(
    private val margin: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = margin
        outRect.right = margin

        if (parent.getChildAdapterPosition(view) != parent.childCount) {
            outRect.bottom = margin
        }

    }

}