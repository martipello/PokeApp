package com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation

import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.CircleCardView

class ScrollAwareFilerFab(
    private val recyclerView: RecyclerView,
    val circleCardView: CircleCardView,
    val circleCardViewParent: ViewGroup
) {

    var scrolling : Boolean = false

    fun start() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        scrolling = false
                        shouldShow()
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        scrolling = true
                        hide()
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                    }
                }
            }
        })
    }

    fun hide() {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(circleCardView)
        TransitionManager.beginDelayedTransition(circleCardViewParent, transition)
        circleCardView.visibility = View.GONE
    }

    private fun show() {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(circleCardView)
        TransitionManager.beginDelayedTransition(circleCardViewParent, transition)
        circleCardView.visibility = View.VISIBLE
    }

    fun shouldShow() {
        val handler = Handler()
        handler.postDelayed({
            if (!scrolling) {
                scrolling = false
                show()
            }
        }, 1)
    }
}