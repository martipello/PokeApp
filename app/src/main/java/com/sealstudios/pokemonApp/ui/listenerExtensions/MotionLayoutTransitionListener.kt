package com.sealstudios.pokemonApp.ui.listenerExtensions

import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


suspend fun MotionLayout.awaitTransitionEnd() =
        suspendCancellableCoroutine<Unit> { continuation ->

            val listener = object : MotionLayout.TransitionListener {
                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                }

                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
            }
            continuation.invokeOnCancellation { this.removeTransitionListener(listener) }
            this.addTransitionListener(listener)

        }



