package com.sealstudios.pokemonApp.ui.animationListeners

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


suspend fun Animator.startAndWait() = suspendCancellableCoroutine<Unit> { continuation ->
    continuation.invokeOnCancellation { cancel() }

    this.addListener(object : AnimatorListenerAdapter() {

        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator?) {
            endedSuccessfully = false
        }

        override fun onAnimationStart(animation: Animator?) {

            animation?.removeListener(this)

            if (continuation.isActive) {
                // If the coroutine is still active...
                if (endedSuccessfully) {
                    // ...and the Animator ended successfully, resume the coroutine
                    continuation.resume(Unit)
                } else {
                    // ...and the Animator was cancelled, cancel the coroutine too
                    continuation.cancel()
                }
            }
        }
    })
    start()
}


suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { continuation ->

    continuation.invokeOnCancellation { cancel() }

    this.addListener(object : AnimatorListenerAdapter() {
        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator) {

            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator) {

            animation.removeListener(this)

            if (continuation.isActive) {
                // If the coroutine is still active...
                if (endedSuccessfully) {
                    // ...and the Animator ended successfully, resume the coroutine
                    continuation.resume(Unit)
                } else {
                    // ...and the Animator was cancelled, cancel the coroutine too
                    continuation.cancel()
                }
            }
        }
    })

}
