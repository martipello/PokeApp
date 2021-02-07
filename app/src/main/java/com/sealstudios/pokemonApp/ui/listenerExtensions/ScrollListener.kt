package com.sealstudios.pokemonApp.ui.listenerExtensions

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun RecyclerView.awaitScrollEnd() {
    // If a smooth scroll has just been started, it won't actually start until the next
    // animation frame, so we'll await that first
    awaitFrame()
    // Now we can check if we're actually idle. If so, return now
    if (scrollState == RecyclerView.SCROLL_STATE_IDLE) return

    suspendCancellableCoroutine<Unit> { cont ->
        cont.invokeOnCancellation {
            // If the coroutine is cancelled, stop the RecyclerView scrolling
            stopScroll()
        }

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Make sure we remove the listener so we don't keep leak the
                    // coroutine continuation
                    recyclerView.removeOnScrollListener(this)
                    // Finally, resume the coroutine
                    cont.resume(Unit)
                }
            }
        })
    }
}

suspend fun RecyclerView.whileScrolling() {
    // If a smooth scroll has just been started, it won't actually start until the next
    // animation frame, so we'll await that first
    awaitFrame()
    // Now we can check if we're actually idle. If so, return now

    suspendCancellableCoroutine<Unit> { cont ->
        cont.invokeOnCancellation {
            // If the coroutine is cancelled, stop the RecyclerView scrolling
            stopScroll()
        }

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Make sure we remove the listener so we don't keep leak the
                    // coroutine continuation
//                    recyclerView.removeOnScrollListener(this)
                    // Finally, resume the coroutine
//                    cont.resume(Unit)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
}
