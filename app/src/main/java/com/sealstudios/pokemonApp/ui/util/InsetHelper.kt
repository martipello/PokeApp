package com.sealstudios.pokemonApp.ui.util

import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.*

fun View.doOnApplyWindowInsetPadding(f: (View, WindowInsets, InitialPadding) -> Unit) {
    // Create a snapshot of the view's padding state
    val initialPadding = recordInitialPaddingForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    setOnApplyWindowInsetsListener { v, insets ->
        f(v, insets, initialPadding)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

fun View.doOnApplyWindowInsetMargin(f: (View, WindowInsets, ViewGroup.MarginLayoutParams) -> Unit) {

    val initialLayoutParams = recordInitialLayoutParams(this)

    setOnApplyWindowInsetsListener { view, insets ->
        f(view, insets, initialLayoutParams)
        insets
    }

}

data class InitialPadding(val left: Int, val top: Int,
                          val right: Int, val bottom: Int)

data class InitialMargin(val left: Int, val top: Int,
                          val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun recordInitialLayoutParams(view: View) : ViewGroup.MarginLayoutParams {
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

fun View.addSystemWindowInsetToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(paddingLeft, paddingTop, paddingRight, paddingBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
//        @deprecated Use {@link #getInsets(int)} with {@link Type#systemBars()} instead insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.updatePadding(
            left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
            top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
            right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
            bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
        )

        insets
    }
}

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(marginLeft, marginTop, marginRight, marginBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            (this as? ViewGroup.MarginLayoutParams)?.let {
                updateMargins(
                    left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                    top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                    right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                    bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
                )
            }
        }

        insets
    }
}


fun View.alignBelowStatusBar() {
    this.setOnApplyWindowInsetsListener { view, insets ->
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = insets.systemWindowInsetTop
        view.layoutParams = params
        insets
    }
}


//fun View.setWindowInsets(){
//    this.setOnApplyWindowInsetsListener { _, windowInsets ->
//        val systemWindowInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//        // It's also possible to use multiple types
//        val insets = windowInsets.getInsets(
//            WindowInsetsCompat.Type.ime() or
//                    WindowInsetsCompat.Type.systemGestures()
//        )
//        windowInsets
//    }
//}