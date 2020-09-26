package com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation

import android.os.Handler
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.CircleCardView

class ScrollAwareFilerFab(
    private val recyclerView: RecyclerView,
    val circleCardView: CircleCardView,
    val circleCardViewParent: ViewGroup,
    val scrollAwareFilterFabAnimationListener: FabFilterAnimationListener
) {

    var scrolling: Boolean = false
    var isExpanded: Boolean = false

    fun start() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (!isExpanded) {
                            scrolling = false
                            shouldShow()
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (!isExpanded) {
                            scrolling = true
                            circleCardView.slideToHide(
                                circleCardViewParent,
                                scrollAwareFilterFabAnimationListener
                            )
                        }
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                    }
                }
            }
        })
    }

    fun shouldShow() {
        val handler = Handler()
        handler.postDelayed({
            if (!scrolling) {
                scrolling = false
                circleCardView.slideToShow(circleCardView, scrollAwareFilterFabAnimationListener)
            }
        }, 1000)
    }
}