package com.sealstudios.pokemonApp.ui.util

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.sealstudios.pokemonApp.R

class ColorStateFactory {
    companion object {
        fun buildColorState(lightVibrantColor: Int, context: Context): ColorStateList {
            return ColorStateList(
                    arrayOf(
                            intArrayOf(android.R.attr.state_selected),
                            intArrayOf(-android.R.attr.state_selected),
                    ),
                    intArrayOf(
                            lightVibrantColor,
                            ContextCompat.getColor(context, R.color.primaryLightColor)
                    )
            )
        }

        fun buildTextColorState(context: Context, isNightMode: Boolean = false): ColorStateList {
            return ColorStateList(
                    arrayOf(
                            intArrayOf(android.R.attr.state_selected),
                            intArrayOf(-android.R.attr.state_selected)
                    ),
                    intArrayOf(
                            ContextCompat.getColor(context, if (isNightMode) R.color.secondaryTextColor else R.color.primaryTextColor),
                            ContextCompat.getColor(context, if (isNightMode) R.color.primaryTextColor else R.color.secondaryTextColor),
                    )
            )
        }
    }
}