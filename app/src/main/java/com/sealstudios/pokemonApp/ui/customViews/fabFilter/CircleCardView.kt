package com.sealstudios.pokemonApp.ui.customViews.fabFilter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
import com.sealstudios.pokemonApp.ui.customViews.fabFilter.animation.FabFilterAnimationListener
import io.codetail.animation.arcanimator.ArcAnimator
import io.codetail.animation.arcanimator.Side
import kotlin.math.hypot
import kotlin.math.min


class CircleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var circleViewStartY: Float = 0f
    private var circleViewStartX: Float = 0f

    init {
        setStartXAndStartY()
    }

    private fun setStartXAndStartY() {
        this.post {
            val location = IntArray(2)
            this.getLocationOnScreen(location)
            circleViewStartX = location[0].toFloat() + this.width / 2
            circleViewStartY = location[1].toFloat() + this.height / 2
        }
    }

    override fun setRadius(radius: Float) {
        val r = if (radius > height / 2 || radius > width / 2) min(height, width) / 2f
        else radius
        super.setRadius(r)
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets? {
        val childCount = childCount
        for (index in 0 until childCount) getChildAt(index).dispatchApplyWindowInsets(insets)
        return insets
    }

    fun slideToHide(circleCardViewParent: ViewGroup, listener: FabFilterAnimationListener?) {
        TransitionManager.beginDelayedTransition(
            circleCardViewParent,
            createSlideTransition(listener)
        )
        this.visibility = View.GONE
    }

    fun slideToShow(circleCardViewParent: ViewGroup, listener: FabFilterAnimationListener?) {
        TransitionManager.beginDelayedTransition(
            circleCardViewParent,
            createSlideTransition(listener)
        )
        this.visibility = View.VISIBLE
    }

    private fun createSlideTransition(listener: FabFilterAnimationListener?): Transition {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.addTarget(this)
        transition.addListener(getSlideTransitionListener(listener))
        return transition
    }

    fun circleReveal(
        listener: FabFilterAnimationListener? = null,
        startAtX: Int = width / 2,
        startAtY: Int = height / 2
    ): Animator {

        val endRadius =
            hypot(
                width.toDouble(),
                height.toDouble()
            ).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            this, startAtX, startAtY,
            0f,
            endRadius.toFloat()
        )

        visibility = View.VISIBLE
        listener?.let {
            anim.addListener(getCircleRevealAnimatorListener(it))
        }
        return anim
    }

    fun circleHide(
        listener: FabFilterAnimationListener? = null,
        endAtX: Int = width / 2,
        endAtY: Int = height / 2
    ): Animator {

        val startRadius =
            hypot(
                width.toDouble(),
                height.toDouble()
            ).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            this, endAtX, endAtY,
            startRadius.toFloat(),
            0f
        )
        listener?.let {
            // If this listener is null make view invisible on ending animation
            // visibility = View.INVISIBLE
            anim.addListener(getCircleHideAnimatorListener(listener))
        }
        return anim
    }

    fun arcAnimateFilterFabIn(nestView: View, listener: FabFilterAnimationListener?): ArcAnimator {
        val arcAnimator = ArcAnimator.createArcAnimator(
            this,
            nestView,
            70f,
            Side.LEFT
        )
        arcAnimator.addListener(animatorListener(listener))
        return arcAnimator
    }

    fun arcAnimateFilterFabOut(
        listener: FabFilterAnimationListener?
    ): ArcAnimator {
        val arcAnimator = ArcAnimator.createArcAnimator(
            this,
            circleViewStartX,
            circleViewStartY,
            70f,
            Side.LEFT
        )
        arcAnimator.addListener(animatorListener(listener))
        return arcAnimator
    }

    private fun animatorListener(listener: FabFilterAnimationListener?): com.nineoldandroids.animation.Animator.AnimatorListener? {
        return object : Animator.AnimatorListener,
            com.nineoldandroids.animation.Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                listener?.onArcAnimationFinished()
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                listener?.onArcAnimationStarted()
            }

            override fun onAnimationRepeat(animation: com.nineoldandroids.animation.Animator?) {}
            override fun onAnimationEnd(animation: com.nineoldandroids.animation.Animator?) {
                listener?.onArcAnimationFinished()
            }

            override fun onAnimationCancel(animation: com.nineoldandroids.animation.Animator?) {}
            override fun onAnimationStart(animation: com.nineoldandroids.animation.Animator?) {
                listener?.onArcAnimationStarted()
            }
        }
    }

    private fun getCircleHideAnimatorListener(listener: FabFilterAnimationListener?): Animator.AnimatorListener {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.INVISIBLE
                listener?.onCircleHideAnimationFinished()
            }

            override fun onAnimationStart(animation: Animator?) {
                listener?.onCircleHideAnimationStarted()
            }
        }
    }

    private fun getCircleRevealAnimatorListener(listener: FabFilterAnimationListener?): Animator.AnimatorListener {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                listener?.onCircleRevealAnimationFinished()
            }

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                listener?.onCircleRevealAnimationStarted()
            }
        }
    }

    private fun getSlideTransitionListener(listener: FabFilterAnimationListener?): Transition.TransitionListener {
        return object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                listener?.onSlideHideAnimationFinished()
            }

            override fun onTransitionResume(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionStart(transition: Transition) {
                listener?.onSlideHideAnimationStarted()
            }
        }
    }

}
