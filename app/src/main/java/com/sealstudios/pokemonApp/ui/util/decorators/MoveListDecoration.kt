package com.sealstudios.pokemonApp.ui.util.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.GenerationHeaderViewHolder
import com.sealstudios.pokemonApp.ui.adapter.viewHolders.MoveViewHolder

class MoveListDecoration constructor(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        when (parent.getChildViewHolder(view)) {
            is MoveViewHolder -> {
                outRect.bottom = margin
            }
            is GenerationHeaderViewHolder -> {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = margin * 2
                }
                outRect.bottom = margin * 2
            }
        }

    }
}