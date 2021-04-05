package com.sealstudios.pokemonApp.database.`object`

import android.annotation.SuppressLint

enum class EvolutionTrigger(val id: Int) {

    LEVEL_UP(id = 1),
    TRADE(id = 2),
    USE_ITEM(id = 3),
    SHED(id = 4),
    OTHER(id = 5);

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