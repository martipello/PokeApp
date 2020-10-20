package com.sealstudios.pokemonApp.ui.adapter.layoutManagers

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NoScrollLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        return false
    }
}