package com.sealstudios.pokemonApp.ui.extensions

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

var animated: AnimatedVectorDrawableCompat? = null

internal fun ImageView.applyLoopingAnimatedVectorDrawable(@DrawableRes avdResId: Int) {
    if (animated == null){
        val animated = AnimatedVectorDrawableCompat.create(context, avdResId)
        animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                this@applyLoopingAnimatedVectorDrawable.post { animated.start() }
            }
        })
        this.setImageDrawable(animated)
        animated?.start()
    }
}
