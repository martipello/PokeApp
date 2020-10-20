package com.sealstudios.pokemonApp.ui.listenerExtensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import io.codetail.animation.arcanimator.ArcAnimator
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


//private fun animatorListener(listener: FabFilterAnimationListener?): com.nineoldandroids.animation.Animator.AnimatorListener? {
//    return object : Animator.AnimatorListener,
//        com.nineoldandroids.animation.Animator.AnimatorListener {
//        override fun onAnimationRepeat(animation: Animator?) {}
//        override fun onAnimationEnd(animation: Animator?) {
//            listener?.onArcAnimationFinished()
//        }
//
//        override fun onAnimationCancel(animation: Animator?) {}
//        override fun onAnimationStart(animation: Animator?) {
//            listener?.onArcAnimationStarted()
//        }
//
//        override fun onAnimationRepeat(animation: com.nineoldandroids.animation.Animator?) {}
//        override fun onAnimationEnd(animation: com.nineoldandroids.animation.Animator?) {
//            listener?.onArcAnimationFinished()
//        }
//
//        override fun onAnimationCancel(animation: com.nineoldandroids.animation.Animator?) {}
//        override fun onAnimationStart(animation: com.nineoldandroids.animation.Animator?) {
//            listener?.onArcAnimationStarted()
//        }
//    }
//}

suspend fun ArcAnimator.awaitEnd() = suspendCancellableCoroutine<Unit> { continuation ->

    continuation.invokeOnCancellation { cancel() }

    this.addListener(object : AnimatorListenerAdapter(),
        com.nineoldandroids.animation.Animator.AnimatorListener {
        private var endedSuccessfully = true

        override fun onAnimationEnd(animation: com.nineoldandroids.animation.Animator?) {

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

        override fun onAnimationCancel(animation: com.nineoldandroids.animation.Animator?) {
            endedSuccessfully = false
        }

        override fun onAnimationStart(animation: com.nineoldandroids.animation.Animator?) {}
        override fun onAnimationRepeat(animation: com.nineoldandroids.animation.Animator?) {}
    })

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
