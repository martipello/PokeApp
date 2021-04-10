package com.sealstudios.pokemonApp.util.extensions

fun List<String>.names(): String {
    return this.toSet().joinToString {
        it.capitalize()
    }
}
