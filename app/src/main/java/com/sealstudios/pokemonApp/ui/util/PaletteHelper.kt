package com.sealstudios.pokemonApp.ui.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.sealstudios.pokemonApp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaletteHelper {
    companion object {
        suspend fun setLightAndDarkVibrantColorForBitmap(bitmap: Bitmap,
                                                         context: Context,
                                                         setColoredElements: suspend (lightVibrantColor: Int, darkVibrantColor: Int) -> Unit) {
            withContext(Dispatchers.Default) {
                val white: Int = ContextCompat.getColor(context, R.color.white)
                Palette.Builder(bitmap).generate {
                    val lightVibrantColor =
                            it?.lightVibrantSwatch?.rgb ?: it?.dominantSwatch?.rgb ?: white
                    val darkVibrantColor =
                            it?.darkVibrantSwatch?.rgb ?: it?.dominantSwatch?.rgb ?: white
                    CoroutineScope(Dispatchers.Default).launch {
                        setColoredElements(lightVibrantColor, darkVibrantColor)
                    }
                }
            }
        }
    }
}