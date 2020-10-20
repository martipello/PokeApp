package com.sealstudios.pokemonApp.ui.animationListeners

import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


suspend fun TransitionSet.awaitTransitionEnd(executeBeforeReturn: (CancellableContinuation<Unit>) -> Unit) =
    suspendCancellableCoroutine<Unit> { continuation ->

        val listener = object : TransitionListenerAdapter() {
            private var endedSuccessfully = true

            override fun onTransitionCancel(transition: Transition) {
                super.onTransitionCancel(transition)
                endedSuccessfully = false
            }

            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                transition.removeListener(this)

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
        }
        continuation.invokeOnCancellation { removeListener(listener) }
        this.addListener(listener)
        executeBeforeReturn(continuation)
    }

