package com.sealstudios.pokemonApp.ui.util

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.sealstudios.pokemonApp.R

class ColorStateFactory {
    companion object {
        fun buildColorState(lightVibrantColor: Int): ColorStateList {
            return ColorStateList(
                    arrayOf(
                            intArrayOf(android.R.attr.state_selected),
                            intArrayOf(-android.R.attr.state_selected)
                    ),
                    intArrayOf(
                            lightVibrantColor,
                            android.R.color.transparent
                    )
            )
        }
        fun buildTextColorState(context: Context): ColorStateList {
            return ColorStateList(
                    arrayOf(
                            intArrayOf(android.R.attr.state_selected),
                            intArrayOf(-android.R.attr.state_selected)
                    ),
                    intArrayOf(
                            ContextCompat.getColor(context, R.color.primaryTextColor),
                            ContextCompat.getColor(context, R.color.secondaryTextColor),
                    )
            )
        }
    }
}