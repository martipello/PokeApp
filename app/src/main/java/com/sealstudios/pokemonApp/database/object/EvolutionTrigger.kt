package com.sealstudios.pokemonApp.database.`object`

import android.annotation.SuppressLint

enum class EvolutionTrigger(val id: Int, val displayName: String) {

    LEVEL_UP(id = 1, displayName = "Level Up"),
    TRADE(id = 2, displayName = "Trade"),
    USE_ITEM(id = 3, displayName = "Use Item"),
    SHED(id = 4, displayName = "Shed"),
    OTHER(id = 5, displayName = "Other");

    companion object {

        @SuppressLint("DefaultLocale")
        fun getEvolutionTrigger(triggerId: Int): EvolutionTrigger {
            return when (triggerId) {
                1 -> LEVEL_UP
                2 -> TRADE
                3 -> USE_ITEM
                4 -> SHED
                else -> OTHER
            }
        }

    }


}