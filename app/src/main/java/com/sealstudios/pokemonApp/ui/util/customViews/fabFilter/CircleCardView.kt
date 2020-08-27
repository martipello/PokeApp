package com.sealstudios.pokemonApp.ui.util.customViews.fabFilter

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.WindowInsets
import com.google.android.material.card.MaterialCardView
import kotlin.math.min


class CircleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    override fun setRadius(radius: Float) {
        super.setRadius(
            if (radius > height / 2 || radius > width / 2) min(height, width) / 2f
            else radius
        )
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets? {
        val childCount = childCount
        for (index in 0 until childCount) getChildAt(index).dispatchApplyWindowInsets(insets)
        return insets
    }

}