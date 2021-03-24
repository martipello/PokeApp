package com.sealstudios.pokemonApp.util.extensions

import java.util.*

fun String.removeWhiteSpace(): String {
    return this.replace("\\s".toRegex(), " ").trim()
}

fun String.capitalize(): String {
    return this.capitalize(Locale.ROOT)
}

fun String.toLowerCase(): String {
    return this.toLowerCase(Locale.ROOT)
}

fun String.toUpperCase(): String {
    return this.toUpperCase(Locale.ROOT)
}

fun String.getIdFromUrl(): Int {
    val stringList = split('/')
    if (stringList.size >= 2) {
        return stringList[stringList.size - 2].toInt()
    }
    return -1
}