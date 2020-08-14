package com.sealstudios.pokemonApp.ui.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class AdaptiveHeightScrollingViewBehavior(
    context: Context,
    attrs: AttributeSet
) : AppBarLayout.ScrollingViewBehavior(context, attrs) {

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        (dependency as? AppBarLayout)?.let { appBar ->
            val appBarHeight = appBar.height + appBar.y
            child.layoutParams.height = (parent.height - appBarHeight).toInt()
            child.requestLayout()
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }
}