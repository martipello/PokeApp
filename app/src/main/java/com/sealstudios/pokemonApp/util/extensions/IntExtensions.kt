package com.sealstudios.pokemonApp.util.extensions

import java.lang.NumberFormatException
import java.util.*

fun Int?.isNotNullOrNegative(): Boolean {
    return this != null && this > 0.0
}