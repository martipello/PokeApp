package com.sealstudios.pokemonApp.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

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