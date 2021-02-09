package com.sealstudios.pokemonApp.ui.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


val <A, B> Pair<A, B>.dominantColor: A get() = this.first
val <A, B> Pair<A, B>.lightVibrantColor: B get() = this.second

class ColorViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var dominantAndLightVibrantColors: MutableLiveData<Pair<Int, Int>> = getViewColors()

    private fun getViewColors(): MutableLiveData<Pair<Int, Int>> {
        val dominantColor = savedStateHandle.get<Int>(dominantColorKey) ?: 0
        val lightVibrantColor = savedStateHandle.get<Int>(lightVibrantColorKey)
                ?: 0
        return MutableLiveData(dominantColor to lightVibrantColor)
    }

    fun setViewColors(dominantColor: Int, lightVibrantColor: Int) {
        dominantAndLightVibrantColors.value = dominantColor to lightVibrantColor
        savedStateHandle.set(lightVibrantColorKey, lightVibrantColor)
        savedStateHandle.set(dominantColorKey, dominantColor)
    }

    companion object {
        private const val lightVibrantColorKey: String = "lightVibrantColor"
        private const val dominantColorKey: String = "dominantColor"
    }

}