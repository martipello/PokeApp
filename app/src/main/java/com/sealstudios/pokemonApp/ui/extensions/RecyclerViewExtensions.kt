package com.sealstudios.pokemonApp.ui.extensions

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeItemDecorations() {
    while (this.itemDecorationCount > 0) {
        this.removeItemDecorationAt(0)
    }
}