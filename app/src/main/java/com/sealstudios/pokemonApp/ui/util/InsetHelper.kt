package com.sealstudios.pokemonApp.ui.util

import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets

fun View.doOnApplyWindowInsetPadding(f: (View, WindowInsets, InitialPadding) -> Unit) {

    val initialPadding = recordInitialPaddingForView(this)

    setOnApplyWindowInsetsListener { v, insets ->
        f(v, insets, initialPadding)
        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.doOnApplyWindowInsetMargin(f: (View, WindowInsets, ViewGroup.MarginLayoutParams) -> Unit) {

    val initialLayoutParams = recordInitialLayoutParams(this)

    setOnApplyWindowInsetsListener { view, insets ->
        f(view, insets, initialLayoutParams)
        insets
    }

}

data class InitialPadding(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int
)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialLayoutParams(view: View): ViewGroup.MarginLayoutParams {
    return view.layoutParams as ViewGroup.MarginLayoutParams
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

//@BindingAdapter(
//    "bind:paddingLeftSystemWindowInsets",
//    "bind:paddingTopSystemWindowInsets",
//    "bind:paddingRightSystemWindowInsets",
//    "bind:paddingBottomSystemWindowInsets",
//    requireAll = false
//)
//fun applySystemWindows(
//    view: View,
//    applyLeft: Boolean,
//    applyTop: Boolean,
//    applyRight: Boolean,
//    applyBottom: Boolean
//) {
//    view.doOnApplyWindowInsetPadding { view, insets, padding ->
//        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
//        val top = if (applyTop) insets.systemWindowInsetTop else 0
//        val right = if (applyRight) insets.systemWindowInsetRight else 0
//        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0
//
//        view.setPadding(
//            padding.left + left,
//            padding.top + top,
//            padding.right + right,
//            padding.bottom + bottom
//        )
//    }
//}

