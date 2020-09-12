package com.sealstudios.pokemonApp.ui.customViews.fabFilter

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import com.google.android.material.card.MaterialCardView
import kotlin.math.min


class CircleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    override fun setRadius(radius: Float) {
        val r = if (radius > height / 2 || radius > width / 2) min(height, width) / 2f
        else radius
        super.setRadius(r)
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets? {
        val childCount = childCount
        for (index in 0 until childCount) getChildAt(index).dispatchApplyWindowInsets(insets)
        return insets
    }

}