package com.sealstudios.pokemonApp.database.`object`

import android.annotation.SuppressLint

enum class RelativePhysicalStats(val value: Int) {
    FAVOR_ATTACK(value = 1),
    FAVOR_DEFENCE(value = 0),
    NEUTRAL(value = -1);

    companion object {

        @SuppressLint("DefaultLocale")
        fun getRelativePhysicalStats(relativePhysicalStats: Int): RelativePhysicalStats {
            return when(relativePhysicalStats){
                0 -> FAVOR_DEFENCE
                1 -> FAVOR_ATTACK
                else -> NEUTRAL
            }
        }

    }
}